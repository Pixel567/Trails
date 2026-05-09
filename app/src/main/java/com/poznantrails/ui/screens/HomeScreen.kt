package com.poznantrails.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.poznantrails.data.CYCLING_TRAILS
import com.poznantrails.data.HIKING_TRAILS
import com.poznantrails.data.Trail
import com.poznantrails.data.TrailType
import com.poznantrails.ui.components.TrailCard
import com.poznantrails.ui.components.TrailTypeButton
import com.poznantrails.ui.theme.LocalAppPalette
import com.poznantrails.viewmodel.TrailsUiState

@Composable
fun HomeScreen(
    uiState: TrailsUiState,
    onTypeToggle: (TrailType) -> Unit,
    onTrailClick: (Trail) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    val trails = when (uiState.expandedType) {
        TrailType.HIKING  -> HIKING_TRAILS
        TrailType.CYCLING -> CYCLING_TRAILS
        null              -> emptyList()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)) {
                Text("Trasy", style = MaterialTheme.typography.headlineLarge, color = palette.onSurface)
                Text("Poznań", style = MaterialTheme.typography.titleLarge, color = palette.onSurfaceVariant)
            }
        }

        item {
            TrailTypeButton(
                type       = TrailType.HIKING,
                isExpanded = uiState.expandedType == TrailType.HIKING,
                trailCount = HIKING_TRAILS.size,
                onClick    = { onTypeToggle(TrailType.HIKING) }
            )
        }

        item {
            TrailTypeButton(
                type       = TrailType.CYCLING,
                isExpanded = uiState.expandedType == TrailType.CYCLING,
                trailCount = CYCLING_TRAILS.size,
                onClick    = { onTypeToggle(TrailType.CYCLING) }
            )
        }

        if (uiState.expandedType != null && trails.isNotEmpty()) {
            item {
                val label = if (uiState.expandedType == TrailType.HIKING) "Trasy piesze" else "Trasy rowerowe"
                Text(
                    "$label · ${trails.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 4.dp)
                )
            }

            items(trails, key = { it.id }) { trail ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    TrailCard(
                        trail      = trail,
                        isSelected = uiState.selectedTrail?.id == trail.id,
                        onClick    = { onTrailClick(trail) }
                    )
                }
            }
        }

        if (uiState.expandedType == null) {
            item {
                Surface(
                    shape  = RoundedCornerShape(14.dp),
                    color  = palette.hikingSoft,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "Wybierz rodzaj trasy, aby zobaczyć dostępne szlaki w Poznaniu",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
