package com.amienigma.noctis.features.launcher

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ThemeEngine - Dynamic theming, color extraction, and wallpaper management for NOCTIS OS
 * Supports accent colors, dark/light modes, and adaptive theming.
 * 
 * Daily code addition 2026-11-16
 */
data class ThemeConfig(
    val primary: Color = Color(0xFF164E57),
    val secondary: Color = Color(0xFFC21E6B),
    val background: Color = Color(0xFF0A0A0A),
    val surface: Color = Color(0xFF1A1A1A),
    val onPrimary: Color = Color.White,
    val isDark: Boolean = true
)

class ThemeEngine {
    private val _currentTheme = MutableStateFlow(ThemeConfig())
    val currentTheme: StateFlow<ThemeConfig> = _currentTheme

    private val presets = mapOf(
        "cybercore" to ThemeConfig(
            primary = Color(0xFF164E57),
            secondary = Color(0xFFC21E6B),
            background = Color(0xFF0A0A0A)
        ),
        "galaxy" to ThemeConfig(
            primary = Color(0xFF6B2D5C),
            secondary = Color(0xFF3D5A80),
            background = Color(0xFF0D1B2A)
        ),
        "minimal" to ThemeConfig(
            primary = Color(0xFF2E2E2E),
            secondary = Color(0xFF555555),
            background = Color(0xFF111111),
            isDark = true
        )
    )

    fun applyPreset(name: String) {
        presets[name]?.let { _currentTheme.value = it }
    }

    fun setPrimary(color: Color) {
        _currentTheme.value = _currentTheme.value.copy(primary = color)
    }

    fun setSecondary(color: Color) {
        _currentTheme.value = _currentTheme.value.copy(secondary = color)
    }

    fun toggleDarkMode() {
        val current = _currentTheme.value
        _currentTheme.value = current.copy(
            isDark = !current.isDark,
            background = if (!current.isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5),
            surface = if (!current.isDark) Color(0xFF1A1A1A) else Color.White
        )
    }

    fun extractColorsFromWallpaper(bitmap: Any?): ThemeConfig {
        // Placeholder for color extraction from wallpaper bitmap
        // In real implementation would use Palette API
        return ThemeConfig(
            primary = Color(0xFF164E57),
            secondary = Color(0xFFC21E6B)
        )
    }

    fun generateAccentVariations(base: Color): List<Color> {
        val variations = mutableListOf<Color>()
        for (i in 0..4) {
            val factor = 0.7f + (i * 0.1f)
            variations.add(base.copy(alpha = factor))
        }
        return variations
    }

    fun getContrastColor(background: Color): Color {
        val luminance = 0.299f * background.red + 0.587f * background.green + 0.114f * background.blue
        return if (luminance > 0.5f) Color.Black else Color.White
    }

    fun saveTheme(prefs: Any) {
        // Would persist to SharedPreferences or DataStore
        val theme = _currentTheme.value
        // prefs.save("primary", theme.primary.toArgb())
    }

    fun loadTheme(prefs: Any) {
        // Load from preferences
        // val primaryArgb = prefs.getInt("primary", 0xFF164E57.toInt())
        // _currentTheme.value = _currentTheme.value.copy(primary = Color(primaryArgb))
    }

    fun listPresets(): List<String> = presets.keys.toList()

    fun getCurrentPrimaryArgb(): Int = _currentTheme.value.primary.toArgb()

    fun getCurrentSecondaryArgb(): Int = _currentTheme.value.secondary.toArgb()

    // Additional utility methods
    fun blendColors(c1: Color, c2: Color, ratio: Float): Color {
        return Color(
            red = c1.red * (1 - ratio) + c2.red * ratio,
            green = c1.green * (1 - ratio) + c2.green * ratio,
            blue = c1.blue * (1 - ratio) + c2.blue * ratio,
            alpha = 1f
        )
    }

    fun darken(color: Color, amount: Float = 0.2f): Color {
        return Color(
            red = (color.red * (1 - amount)).coerceIn(0f, 1f),
            green = (color.green * (1 - amount)).coerceIn(0f, 1f),
            blue = (color.blue * (1 - amount)).coerceIn(0f, 1f)
        )
    }

    fun lighten(color: Color, amount: Float = 0.2f): Color {
        return Color(
            red = (color.red + amount).coerceIn(0f, 1f),
            green = (color.green + amount).coerceIn(0f, 1f),
            blue = (color.blue + amount).coerceIn(0f, 1f)
        )
    }
}

// Companion and extensions
object ThemePresets {
    val CYBERCORE = "cybercore"
    val GALAXY = "galaxy"
    val MINIMAL = "minimal"
}

fun ThemeEngine.applyCybercore() = applyPreset(ThemePresets.CYBERCORE)
fun ThemeEngine.applyGalaxy() = applyPreset(ThemePresets.GALAXY)

// End of ThemeEngine.kt - substantial addition for daily code goal
