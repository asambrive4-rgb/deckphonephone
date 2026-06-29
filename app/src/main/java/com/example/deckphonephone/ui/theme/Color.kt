package com.example.deckphonephone.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.deckphonephone.deck.application.DeckColorTheme

internal data class DeckColorPalette(
    val main: Color,
    val mainAccent: Color,
    val surface: Color,
    val surfaceSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val success: Color,
    val error: Color,
    val border: Color,
)

val SkyMain = Color(0xFFA1C6EA)
val SkyMainAccent = Color(0xFF2F76B7)
val SkySurface = Color(0xFFF6FAFF)
val SkySurfaceSubtle = Color(0xFFEAF4FF)
val SkyTextPrimary = Color(0xFF1F2A37)
val SkyTextSecondary = Color(0xFF5F6F82)
val SkyAccent = Color(0xFFF7C873)
val SkySuccess = Color(0xFF72C7A5)
val SkyError = Color(0xFFF28B82)
val SkyBorder = Color(0xFFC8DCED)

val ApricotMain = Color(0xFFF4BFA8)
val ApricotMainAccent = Color(0xFFB66B50)
val ApricotSurface = Color(0xFFFFF8F5)
val ApricotSurfaceSubtle = Color(0xFFFCEDE6)
val ApricotTextPrimary = Color(0xFF2F2723)
val ApricotTextSecondary = Color(0xFF75665F)
val ApricotBorder = Color(0xFFEBCFC3)

val MintMain = Color(0xFFA8DCCB)
val MintMainAccent = Color(0xFF3E8F76)
val MintSurface = Color(0xFFF5FFFB)
val MintSurfaceSubtle = Color(0xFFE8F8F2)
val MintTextPrimary = Color(0xFF1F2D29)
val MintTextSecondary = Color(0xFF5B7269)
val MintBorder = Color(0xFFC7E5DA)

val LavenderMain = Color(0xFFC8BCEA)
val LavenderMainAccent = Color(0xFF796CB8)
val LavenderSurface = Color(0xFFFAF8FF)
val LavenderSurfaceSubtle = Color(0xFFF0EBFF)
val LavenderTextPrimary = Color(0xFF292536)
val LavenderTextSecondary = Color(0xFF68617A)
val LavenderBorder = Color(0xFFD8D0F0)

val NightSurface = Color(0xFF101820)
val NightSurfaceSubtle = Color(0xFF182434)
val NightTextPrimary = Color(0xFFF6FAFF)
val NightTextSecondary = Color(0xFFC7D2DE)

internal fun DeckColorTheme.toDeckColorPalette(): DeckColorPalette {
    return when (this) {
        DeckColorTheme.Sky -> DeckColorPalette(
            main = SkyMain,
            mainAccent = SkyMainAccent,
            surface = SkySurface,
            surfaceSubtle = SkySurfaceSubtle,
            textPrimary = SkyTextPrimary,
            textSecondary = SkyTextSecondary,
            accent = SkyAccent,
            success = SkySuccess,
            error = SkyError,
            border = SkyBorder,
        )

        DeckColorTheme.Apricot -> DeckColorPalette(
            main = ApricotMain,
            mainAccent = ApricotMainAccent,
            surface = ApricotSurface,
            surfaceSubtle = ApricotSurfaceSubtle,
            textPrimary = ApricotTextPrimary,
            textSecondary = ApricotTextSecondary,
            accent = SkyAccent,
            success = SkySuccess,
            error = SkyError,
            border = ApricotBorder,
        )

        DeckColorTheme.Mint -> DeckColorPalette(
            main = MintMain,
            mainAccent = MintMainAccent,
            surface = MintSurface,
            surfaceSubtle = MintSurfaceSubtle,
            textPrimary = MintTextPrimary,
            textSecondary = MintTextSecondary,
            accent = SkyAccent,
            success = SkySuccess,
            error = SkyError,
            border = MintBorder,
        )

        DeckColorTheme.Lavender -> DeckColorPalette(
            main = LavenderMain,
            mainAccent = LavenderMainAccent,
            surface = LavenderSurface,
            surfaceSubtle = LavenderSurfaceSubtle,
            textPrimary = LavenderTextPrimary,
            textSecondary = LavenderTextSecondary,
            accent = SkyAccent,
            success = SkySuccess,
            error = SkyError,
            border = LavenderBorder,
        )
    }
}

internal fun DeckColorTheme.previewColor(): Color {
    return toDeckColorPalette().main
}
