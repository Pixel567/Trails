package com.poznantrails.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.poznantrails.data.Difficulty
import com.poznantrails.data.Trail
import com.poznantrails.data.TrailType
import com.poznantrails.ui.theme.LocalAppPalette

@Composable
fun TrailCard(
    trail: Trail,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette     = LocalAppPalette.current
    val isCycling   = trail.type == TrailType.CYCLING
    val typeColor   = if (isCycling) palette.cyclingPrimary else palette.hikingPrimary
    val lightColor  = if (isCycling) palette.cyclingSoft else palette.hikingSoft
    val icon        = if (isCycling) Icons.Default.DirectionsBike else Icons.Default.DirectionsWalk

    val diffColor = when (trail.difficulty) {
        Difficulty.EASY   -> palette.difficultyEasy
        Difficulty.MEDIUM -> palette.difficultyMedium
        Difficulty.HARD   -> palette.difficultyHard
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) lightColor else palette.surface
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, typeColor)
        else
            androidx.compose.foundation.BorderStroke(1.dp, palette.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape  = RoundedCornerShape(12.dp),
                color  = lightColor,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(icon, contentDescription = null, tint = typeColor, modifier = Modifier.size(22.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(trail.name, style = MaterialTheme.typography.titleMedium, color = palette.onSurface)

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Badge(containerColor = lightColor, contentColor = typeColor) {
                        Text(trail.distance, style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Badge(containerColor = diffColor.copy(alpha = 0.12f), contentColor = diffColor) {
                        Text(trail.difficulty.label, style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = palette.onSurfaceVariant
            )
        }
    }
}
