package com.poznantrails.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class SavedTimesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadAll(): List<SavedTime> {
        val raw = prefs.getString(KEY_TIMES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { idx ->
                val obj = array.getJSONObject(idx)
                SavedTime(
                    id        = obj.getString("id"),
                    trailId   = obj.getString("trailId"),
                    trailName = obj.getString("trailName"),
                    trailType = TrailType.valueOf(obj.getString("trailType")),
                    elapsedMs = obj.getLong("elapsedMs"),
                    savedAt   = obj.getLong("savedAt")
                )
            }
        }.getOrDefault(emptyList())
    }

    fun add(trail: Trail, elapsedMs: Long): SavedTime {
        val entry = SavedTime(
            id        = UUID.randomUUID().toString(),
            trailId   = trail.id,
            trailName = trail.name,
            trailType = trail.type,
            elapsedMs = elapsedMs,
            savedAt   = System.currentTimeMillis()
        )
        val current = loadAll().toMutableList()
        current.add(0, entry)
        persist(current)
        return entry
    }

    fun delete(id: String) {
        val current = loadAll().filterNot { it.id == id }
        persist(current)
    }

    fun clear() {
        prefs.edit().remove(KEY_TIMES).apply()
    }

    private fun persist(items: List<SavedTime>) {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject()
                .put("id", item.id)
                .put("trailId", item.trailId)
                .put("trailName", item.trailName)
                .put("trailType", item.trailType.name)
                .put("elapsedMs", item.elapsedMs)
                .put("savedAt", item.savedAt)
            array.put(obj)
        }
        prefs.edit().putString(KEY_TIMES, array.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "poznan_trails_prefs"
        private const val KEY_TIMES  = "saved_times"
    }
}
