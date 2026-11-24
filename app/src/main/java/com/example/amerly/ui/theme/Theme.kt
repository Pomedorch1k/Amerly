
package com.example.amerly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF5275D8),
    onPrimary = Color.White,

    secondary = Color(0xFF526A9F),
    onSecondary = Color.White,

    background = Color(0xFFF5F6FA),
    onBackground = Color(0xFF202438),

    surface = Color.White,
    onSurface = Color(0xFF202438),

    surfaceVariant = Color(0xFFE8EEFF),
    onSurfaceVariant = Color(0xFF74798B),

    primaryContainer = Color(0xFFE8EEFF),
    onPrimaryContainer = Color(0xFF263B68),

    secondaryContainer = Color(0xFFE9F0FA),
    onSecondaryContainer = Color(0xFF263B68),

    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA9BCFF),
    onPrimary = Color(0xFF172B59),

    secondary = Color(0xFFB8C7EB),
    onSecondary = Color(0xFF263653),

    background = Color(0xFF10131B),
    onBackground = Color(0xFFE8EAF2),

    surface = Color(0xFF1B202B),
    onSurface = Color(0xFFE8EAF2),

    surfaceVariant = Color(0xFF293246),
    onSurfaceVariant = Color(0xFFBBC3D5),

    primaryContainer = Color(0xFF263B68),
    onPrimaryContainer = Color(0xFFDCE5FF),

    secondaryContainer = Color(0xFF293750),
    onSecondaryContainer = Color(0xFFDCE5FF),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun AmerlyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
