package com.poznantrails.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.poznantrails.data.SavedTime
import com.poznantrails.data.SavedTimesRepository
import com.poznantrails.data.Trail
import com.poznantrails.data.TrailType
import com.poznantrails.data.WikipediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DescriptionState(
    val isLoading: Boolean = false,
    val text: String? = null,
    val imageUrl: String? = null,
    val error: String? = null
)

data class ActiveStopwatch(
    val trail: Trail,
    val isRunning: Boolean,
    val elapsedMs: Long
)

data class TrailsUiState(
    val expandedType: TrailType? = null,
    val selectedTrail: Trail? = null,
    val descriptions: Map<String, DescriptionState> = emptyMap(),
    val stopwatch: ActiveStopwatch? = null,
    val savedTimes: List<SavedTime> = emptyList()
)

class TrailsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WikipediaRepository()
    private val savedTimesRepository = SavedTimesRepository(application)

    private val _uiState = MutableStateFlow(
        TrailsUiState(savedTimes = savedTimesRepository.loadAll())
    )
    val uiState: StateFlow<TrailsUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null

    fun toggleType(type: TrailType) {
        _uiState.update { state ->
            state.copy(expandedType = if (state.expandedType == type) null else type)
        }
    }

    fun selectTrail(trail: Trail) {
        _uiState.update { it.copy(selectedTrail = trail) }
        fetchDescription(trail)
    }

    fun clearSelectedTrail() {
        _uiState.update { it.copy(selectedTrail = null) }
    }

    fun fetchDescription(trail: Trail) {
        val current = _uiState.value.descriptions[trail.id]
        if (current?.text != null || current?.isLoading == true) return

        _uiState.update { state ->
            state.copy(
                descriptions = state.descriptions + (trail.id to DescriptionState(isLoading = true))
            )
        }

        viewModelScope.launch {
            val result = repository.fetchDescription(trail.wikipediaTitle)
            _uiState.update { state ->
                val descState = result.fold(
                    onSuccess = { DescriptionState(text = it.description, imageUrl = it.imageUrl) },
                    onFailure = { DescriptionState(error = it.message) }
                )
                state.copy(descriptions = state.descriptions + (trail.id to descState))
            }
        }
    }

    fun retryDescription(trail: Trail) {
        _uiState.update { state ->
            state.copy(descriptions = state.descriptions - trail.id)
        }
        fetchDescription(trail)
    }

    fun startStopwatch(trail: Trail) {
        if (_uiState.value.stopwatch != null) return
        _uiState.update {
            it.copy(stopwatch = ActiveStopwatch(trail, isRunning = true, elapsedMs = 0L))
        }
        ensureTickerState()
    }

    fun toggleStopwatch() {
        val current = _uiState.value.stopwatch ?: return
        _uiState.update {
            it.copy(stopwatch = current.copy(isRunning = !current.isRunning))
        }
        ensureTickerState()
    }

    fun finishStopwatch() {
        val current = _uiState.value.stopwatch ?: return
        if (current.elapsedMs > 0L) {
            savedTimesRepository.add(current.trail, current.elapsedMs)
        }
        _uiState.update {
            it.copy(
                stopwatch  = null,
                savedTimes = savedTimesRepository.loadAll()
            )
        }
        ensureTickerState()
    }

    fun deleteSavedTime(id: String) {
        savedTimesRepository.delete(id)
        _uiState.update { it.copy(savedTimes = savedTimesRepository.loadAll()) }
    }

    fun refreshSavedTimes() {
        _uiState.update { it.copy(savedTimes = savedTimesRepository.loadAll()) }
    }

    private fun ensureTickerState() {
        val running = _uiState.value.stopwatch?.isRunning == true
        if (running && tickerJob == null) {
            tickerJob = viewModelScope.launch {
                while (true) {
                    delay(TICK_INTERVAL_MS)
                    _uiState.update { state ->
                        val sw = state.stopwatch ?: return@update state
                        if (sw.isRunning) {
                            state.copy(stopwatch = sw.copy(elapsedMs = sw.elapsedMs + TICK_INTERVAL_MS))
                        } else state
                    }
                }
            }
        } else if (!running) {
            tickerJob?.cancel()
            tickerJob = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
    }

    companion object {
        private const val TICK_INTERVAL_MS = 1000L
    }
}
