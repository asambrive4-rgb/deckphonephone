package com.example.deckphonephone.deck.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

internal object DeckUiColors {
    val categoryScreenBackground: Color
        @Composable get() = MaterialTheme.colorScheme.surface

    val actionScreenBackground: Color
        @Composable get() = MaterialTheme.colorScheme.surface
            .copy(alpha = 0.30f)
            .compositeOver(MaterialTheme.colorScheme.surfaceVariant)

    val categoryCardContainer: Color
        @Composable get() = actionScreenBackground

    val actionCardContainer: Color
        @Composable get() = MaterialTheme.colorScheme.primary
            .copy(alpha = 0.17f)
            .compositeOver(MaterialTheme.colorScheme.surfaceVariant)

    val emptyCardContainer: Color
        @Composable get() = actionCardContainer

    val cardBorder: Color
        @Composable get() = MaterialTheme.colorScheme.outlineVariant

    val cardTitle: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface

    val categoryLabel: Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    val actionLabel: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    val disabledActionLabel: Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    val emptyCardText: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    val connectedBadgeContainer: Color
        @Composable get() = MaterialTheme.colorScheme.secondary

    val connectedBadgeContent: Color
        @Composable get() = MaterialTheme.colorScheme.onSecondary

    val cardGlow: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    val headerTitle: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface

    val headerIcon: Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
}
