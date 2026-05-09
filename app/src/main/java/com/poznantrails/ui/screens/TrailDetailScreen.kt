package com.poznantrails.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.poznantrails.data.Trail
import com.poznantrails.viewmodel.DescriptionState

@Composable
fun TrailDetailScreen(
    trail: Trail,
    descriptionState: DescriptionState?,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    TrailDetailContent(
        trail            = trail,
        descriptionState = descriptionState,
        onBack           = onBack,
        onRetry          = onRetry,
        isTablet         = false,
        modifier         = Modifier.fillMaxSize()
    )
}
