package com.amienigma.noctis.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Central colour tokens from the NOCTIS ARCHITECTURE spec (default "Obsidian" palette). */
object NoctisColors {
    val Background = Color(0xFF050607)
    val BackgroundAlt = Color(0xFF0A0C10)
    val Elevated = Color(0xFF10141A)
    val Surface2 = Color(0xFF171D25)
    val TextPrimary = Color(0xFFF1F4F8)
    val TextSecondary = Color(0xFFA1ABB9)
    val TextMuted = Color(0xFF8A95A6) // lifted from #697586 to keep 4.5:1 contrast
    val Accent = Color(0xFF82A9FF)
    val AccentAlt = Color(0xFFA78BFA)
    val AccentTertiary = Color(0xFF66D9C5)
    val Warning = Color(0xFFF3BB68)
    val Error = Color(0xFFFF727F)
    val Success = Color(0xFF75D6A2)
}

private val Scheme = darkColorScheme(
    primary = NoctisColors.Accent,
    secondary = NoctisColors.AccentAlt,
    tertiary = NoctisColors.AccentTertiary,
    background = NoctisColors.Background,
    surface = NoctisColors.Elevated,
    surfaceVariant = NoctisColors.Surface2,
    onBackground = NoctisColors.TextPrimary,
    onSurface = NoctisColors.TextPrimary,
    onSurfaceVariant = NoctisColors.TextSecondary,
    error = NoctisColors.Error,
)

@Composable
fun NoctisTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}
