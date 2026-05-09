package com.poznantrails.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.poznantrails.data.TrailType
import com.poznantrails.ui.theme.LocalAppPalette

@Composable
fun TrailTypeButton(
    type: TrailType,
    isExpanded: Boolean,
    trailCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette      = LocalAppPalette.current
    val isCycling    = type == TrailType.CYCLING
    val activeColor  = if (isCycling) palette.cyclingPrimary else palette.hikingPrimary
    val lightColor   = if (isCycling) palette.cyclingSoft else palette.hikingSoft
    val icon         = if (isCycling) Icons.Default.DirectionsBike else Icons.Default.DirectionsWalk
    val label        = if (isCycling) "Trasy Rowerowe" else "Trasy Piesze"

    val containerColor = if (isExpanded) activeColor else palette.surface
    val contentColor   = if (isExpanded) Color.White  else palette.onSurface
    val iconBg         = if (isExpanded) Color.White.copy(alpha = 0.2f) else lightColor
    val iconTint       = if (isExpanded) Color.White else activeColor

    val arrowAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "arrow"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape  = RoundedCornerShape(14.dp),
                color  = iconBg,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleLarge, color = contentColor)
                Text(
                    "W Poznaniu · $trailCount tras",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isExpanded) Color.White.copy(alpha = 0.75f) else palette.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (isExpanded) Color.White else palette.onSurfaceVariant,
                modifier = Modifier.rotate(arrowAngle)
            )
        }
    }
}
