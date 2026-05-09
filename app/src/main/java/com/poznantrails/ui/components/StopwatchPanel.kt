package com.poznantrails.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.poznantrails.data.Trail
import com.poznantrails.data.TrailType
import com.poznantrails.ui.theme.LocalAppPalette
import com.poznantrails.util.formatStopwatch
import com.poznantrails.viewmodel.ActiveStopwatch

/**
 * Application-wide stopwatch FAB.
 *
 * - When a stopwatch is **active** (anywhere in the app): shows an extended FAB with the
 *   trail name + live time and Pause/Resume, accompanied by a small Finish FAB on the side.
 * - When **no** stopwatch is active and the user is currently viewing a trail detail
 *   ([viewingTrail] != null): shows a round Play FAB to start a stopwatch for that trail.
 * - Otherwise (no active stopwatch and no trail in focus): renders nothing.
 */
@Composable
fun StopwatchFab(
    active: ActiveStopwatch?,
    viewingTrail: Trail?,
    onStart: (Trail) -> Unit,
    onToggle: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (active != null) {
            val accent = if (active.trail.type == TrailType.CYCLING)
                palette.cyclingPrimary else palette.hikingPrimary

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn(),
                exit  = fadeOut() + scaleOut()
            ) {
                SmallFloatingActionButton(
                    onClick = onFinish,
                    containerColor = palette.difficultyHard,
                    contentColor   = Color.White
                ) {
                    Icon(Icons.Default.Flag, contentDescription = "Zakończ stoper")
                }
            }

            ExtendedFloatingActionButton(
                onClick = onToggle,
                containerColor = accent,
                contentColor   = Color.White,
                icon = {
                    Icon(
                        if (active.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (active.isRunning) "Pauza" else "Wznów"
                    )
                },
                text = {
                    Column {
                        Text(
                            active.trail.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            formatStopwatch(active.elapsedMs),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            )
        } else if (viewingTrail != null) {
            val accent = if (viewingTrail.type == TrailType.CYCLING)
                palette.cyclingPrimary else palette.hikingPrimary

            FloatingActionButton(
                onClick = { onStart(viewingTrail) },
                containerColor = accent,
                contentColor   = Color.White
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Start stopera")
            }
        }
    }
}
