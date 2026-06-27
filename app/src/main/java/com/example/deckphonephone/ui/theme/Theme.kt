package com.example.deckphonephone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SkyMain,
    secondary = SkyAccent,
    tertiary = SkySurfaceSubtle,
    background = NightBackground,
    surface = NightSurface,
    error = SkyError,
    outline = SkyBorder,
    outlineVariant = SkyBorder,
    onPrimary = SkyTextPrimary,
    onSecondary = SkyTextPrimary,
    onTertiary = SkyTextPrimary,
    onBackground = NightTextPrimary,
    onSurface = NightTextPrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = SkyMain,
    secondary = SkyAccent,
    tertiary = SkySurfaceSubtle,
    background = SkyBackground,
    surface = SkySurface,
    surfaceVariant = SkySurfaceSubtle,
    error = SkyError,
    outline = SkyBorder,
    outlineVariant = SkyBorder,
    onPrimary = SkyTextPrimary,
    onSecondary = SkyTextPrimary,
    onTertiary = SkyTextPrimary,
    onBackground = SkyTextPrimary,
    onSurface = SkyTextPrimary,
    onSurfaceVariant = SkyTextSecondary,
)

@Composable
fun DeckphonephoneTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
