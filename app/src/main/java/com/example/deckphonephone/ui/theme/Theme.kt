package com.example.deckphonephone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.deckphonephone.deck.application.DeckColorTheme

private fun DeckColorPalette.toDarkColorScheme() = darkColorScheme(
    primary = main,
    secondary = mainAccent,
    tertiary = accent,
    surface = NightSurface,
    background = NightSurface,
    surfaceVariant = NightSurfaceSubtle,
    primaryContainer = mainAccent,
    error = error,
    outline = border,
    outlineVariant = border,
    onPrimary = textPrimary,
    onSecondary = Color.White,
    onTertiary = textPrimary,
    onPrimaryContainer = NightTextPrimary,
    onSurface = NightTextPrimary,
    onBackground = NightTextPrimary,
    onSurfaceVariant = NightTextSecondary,
)

private fun DeckColorPalette.toLightColorScheme() = lightColorScheme(
    primary = main,
    secondary = mainAccent,
    tertiary = accent,
    surface = surface,
    background = surface,
    surfaceVariant = surfaceSubtle,
    primaryContainer = surfaceSubtle,
    error = error,
    outline = border,
    outlineVariant = border,
    onPrimary = textPrimary,
    onSecondary = Color.White,
    onTertiary = textPrimary,
    onPrimaryContainer = textPrimary,
    onSurface = textPrimary,
    onBackground = textPrimary,
    onSurfaceVariant = textSecondary,
)

@Composable
fun DeckphonephoneTheme(
    darkTheme: Boolean = false,
    colorTheme: DeckColorTheme = DeckColorTheme.Sky,
    content: @Composable () -> Unit,
) {
    val palette = colorTheme.toDeckColorPalette()
    val colorScheme = when {
        darkTheme -> palette.toDarkColorScheme()
        else -> palette.toLightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

fun systemBarSurfaceColor(
    darkTheme: Boolean,
    colorTheme: DeckColorTheme,
): Color {
    return if (darkTheme) {
        NightSurface
    } else {
        colorTheme.toDeckColorPalette().surface
    }
}
