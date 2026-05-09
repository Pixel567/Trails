package com.poznantrails.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatStopwatch(elapsedMs: Long): String {
    val totalSeconds = elapsedMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val tenths = (elapsedMs % 1000) / 100
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

fun formatDuration(elapsedMs: Long): String {
    val totalSeconds = elapsedMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d godz. %02d min %02d s", hours, minutes, seconds)
    } else if (minutes > 0) {
        String.format(Locale.getDefault(), "%d min %02d s", minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%d s", seconds)
    }
}

private val dateFormatter = SimpleDateFormat("d MMM yyyy, HH:mm", Locale("pl", "PL"))

fun formatSavedDate(timestamp: Long): String =
    dateFormatter.format(Date(timestamp))
