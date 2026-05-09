package com.poznantrails.data

data class SavedTime(
    val id: String,
    val trailId: String,
    val trailName: String,
    val trailType: TrailType,
    val elapsedMs: Long,
    val savedAt: Long
)
