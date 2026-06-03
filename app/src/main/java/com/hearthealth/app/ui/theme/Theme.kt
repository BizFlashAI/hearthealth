package com.hearthealth.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val HeartPink = Color(0xFFFF6B9D)
private val HeartPinkDark = Color(0xFFFF8DB5)
private val HeartRed = Color(0xFFE53E6B)
private val SoftWhite = Color(0xFFFFF8FA)
private val DarkBg = Color(0xFF1A1A2E)

private val LightColorScheme = lightColorScheme(
    primary = HeartPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E4),
    onPrimaryContainer = HeartRed,
    secondary = Color(0xFF7C5CBF),
    onSecondary = Color.White,
    background = SoftWhite,
    surface = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

private val DarkColorScheme = darkColorScheme(
    primary = HeartPinkDark,
    onPrimary = Color(0xFF4A0028),
    primaryContainer = HeartRed,
    onPrimaryContainer = Color(0xFFFFD9E4),
    secondary = Color(0xFFCFBCFF),
    onSecondary = Color(0xFF381E72),
    background = DarkBg,
    surface = Color(0xFF1E1E32),
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
)

@Composable
fun HeartHealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
