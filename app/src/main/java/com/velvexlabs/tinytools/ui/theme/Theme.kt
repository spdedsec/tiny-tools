package com.velvexlabs.tinytools.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.velvexlabs.tinytools.data.ThemeChoice

private val SignalOrange = Color(0xFFF28C28)
private val DarkBlack = Color(0xFF101112)
private val DarkCharcoal = Color(0xFF181A1C)
private val DarkGraphite = Color(0xFF232629)
private val DarkText = Color(0xFFF3F0EA)
private val DarkMuted = Color(0xFFA7A7A2)
private val LightCanvas = Color(0xFFF4F1EB)
private val LightSurface = Color(0xFFFFFFFF)
private val LightGraphite = Color(0xFFE5E1D9)
private val LightText = Color(0xFF1A1C1D)
private val LightMuted = Color(0xFF64676A)

private val DarkColors = darkColorScheme(
    primary = SignalOrange,
    onPrimary = Color(0xFF251300),
    background = DarkBlack,
    onBackground = DarkText,
    surface = DarkCharcoal,
    onSurface = DarkText,
    surfaceVariant = DarkGraphite,
    onSurfaceVariant = DarkMuted,
    outline = Color(0xFF3D4144),
    error = Color(0xFFFFB4AB)
)

private val LightColors = lightColorScheme(
    primary = SignalOrange,
    onPrimary = Color(0xFF251300),
    background = LightCanvas,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightGraphite,
    onSurfaceVariant = LightMuted,
    outline = Color(0xFF85847F),
    error = Color(0xFFBA1A1A)
)

private val TinyTypography = Typography(
    displayLarge = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 48.sp, lineHeight = 52.sp),
    displayMedium = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 40.sp),
    headlineLarge = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = androidx.compose.ui.text.TextStyle(fontSize = 17.sp, lineHeight = 24.sp),
    bodyMedium = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, lineHeight = 21.sp),
    labelLarge = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp)
)

@Composable
fun TinyToolsTheme(
    themeChoice: ThemeChoice,
    content: @Composable () -> Unit
) {
    val dark = when (themeChoice) {
        ThemeChoice.SYSTEM -> isSystemInDarkTheme()
        ThemeChoice.LIGHT -> false
        ThemeChoice.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = TinyTypography,
        content = content
    )
}
