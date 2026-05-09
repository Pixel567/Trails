package com.poznantrails.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.poznantrails.data.SavedTime
import com.poznantrails.data.TrailType
import com.poznantrails.ui.theme.LocalAppPalette
import com.poznantrails.util.formatDuration
import com.poznantrails.util.formatSavedDate

@Composable
fun SavedTimesScreen(
    savedTimes: List<SavedTime>,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)) {
                Text("Zapisane czasy", style = MaterialTheme.typography.headlineLarge, color = palette.onSurface)
                Text(
                    if (savedTimes.isEmpty()) "Brak zapisanych pomiarów" else "Ilość pomiarów: ${savedTimes.size}",
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.onSurfaceVariant
                )
            }
        }

        if (savedTimes.isEmpty()) {
            item {
                Surface(
                    shape  = RoundedCornerShape(16.dp),
                    color  = palette.hikingSoft,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.TimerOff,
                            contentDescription = null,
                            tint = palette.hikingPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            "Jeszcze nic tu nie ma",
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.onSurface
                        )
                        Text(
                            "Otwórz dowolną trasę i użyj stopera, aby zapisać swój czas. Pomiary trafią tutaj.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(savedTimes, key = { it.id }) { entry ->
                SavedTimeCard(
                    entry = entry,
                    onDelete = { onDelete(entry.id) }
                )
            }
        }
    }
}

@Composable
private fun SavedTimeCard(
    entry: SavedTime,
    onDelete: () -> Unit
) {
    val palette  = LocalAppPalette.current
    val isCycling = entry.trailType == TrailType.CYCLING
    val accent    = if (isCycling) palette.cyclingPrimary else palette.hikingPrimary
    val soft      = if (isCycling) palette.cyclingSoft else palette.hikingSoft
    val icon      = if (isCycling) Icons.Default.DirectionsBike else Icons.Default.DirectionsWalk

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = soft,
                modifier = Modifier.size(44.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    entry.trailName,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
                    Text(
                        formatDuration(entry.elapsedMs),
                        style = MaterialTheme.typography.bodyMedium,
                        color = accent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    formatSavedDate(entry.savedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Usuń",
                    tint = palette.onSurfaceVariant
                )
            }
        }
    }
}
