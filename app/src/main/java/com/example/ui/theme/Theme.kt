package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    secondary = Cyan80,
    tertiary = Navy80,
    background = VaanMidnight,
    surface = VaanSlateDarkCard,
    onPrimary = VaanMidnight,
    onSecondary = VaanMidnight,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = VaanPrimaryBlue,
    secondary = VaanTeal,
    tertiary = VaanCyanAccent,
    background = VaanSlateBg,
    surface = VaanSlateCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = VaanMidnight,
    onSurface = VaanMidnight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark theme to match the beautiful Vaan brand dark mode preview exactly
    dynamicColor: Boolean = false, // Force brand identity color consistency
    content: @Composable () -> Unit,
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
        typography = Typography,
        content = content
    )
}
