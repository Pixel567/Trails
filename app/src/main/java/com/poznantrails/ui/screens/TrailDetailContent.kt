package com.poznantrails.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.poznantrails.data.Difficulty
import com.poznantrails.data.Trail
import com.poznantrails.data.TrailType
import com.poznantrails.ui.theme.LocalAppPalette
import com.poznantrails.viewmodel.DescriptionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailDetailContent(
    trail: Trail,
    descriptionState: DescriptionState?,
    onBack: (() -> Unit)? = null,
    onRetry: () -> Unit,
    isTablet: Boolean = false,
    modifier: Modifier = Modifier
) {
    val palette    = LocalAppPalette.current
    val isCycling  = trail.type == TrailType.CYCLING
    val typeColor  = if (isCycling) palette.cyclingPrimary else palette.hikingPrimary
    val icon       = if (isCycling) Icons.Default.DirectionsBike else Icons.Default.DirectionsWalk
    val typeLabel  = if (isCycling) "Trasa Rowerowa" else "Trasa Piesza"

    val diffColor = when (trail.difficulty) {
        Difficulty.EASY   -> palette.difficultyEasy
        Difficulty.MEDIUM -> palette.difficultyMedium
        Difficulty.HARD   -> palette.difficultyHard
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (!isTablet && onBack != null) {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.background,
                    navigationIconContentColor = palette.onSurface
                )
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = typeColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (descriptionState?.imageUrl != null) {
                        AsyncImage(
                            model = descriptionState.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier.matchParentSize(),
                            color = typeColor.copy(alpha = 0.6f)
                        ) {}
                    }

                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                            }
                        }
                        Text(
                            typeLabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = androidx.compose.ui.unit.TextUnit(1f, androidx.compose.ui.unit.TextUnitType.Sp)
                            ),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            trail.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    icon = Icons.Default.Route,
                    value = trail.distance,
                    label = "Długość",
                    iconColor = typeColor,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.SignalCellularAlt,
                    value = trail.difficulty.label,
                    label = "Trudność",
                    iconColor = diffColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = typeColor, modifier = Modifier.size(20.dp))
                        Text(
                            "O tym miejscu",
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.onSurface
                        )
                    }

                    when {
                        descriptionState == null || descriptionState.isLoading -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = typeColor)
                                Text("Pobieranie z Wikipedii…", color = palette.onSurfaceVariant)
                            }
                        }
                        descriptionState.error != null -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = palette.error, modifier = Modifier.size(18.dp))
                                    Text(descriptionState.error, color = palette.error)
                                }
                                OutlinedButton(onClick = onRetry) {
                                    Text("Spróbuj ponownie")
                                }
                            }
                        }
                        descriptionState.text != null -> {
                            Text(
                                descriptionState.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = palette.surface,
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = palette.onSurface)
            Text(label, style = MaterialTheme.typography.labelSmall, color = palette.onSurfaceVariant)
        }
    }
}
