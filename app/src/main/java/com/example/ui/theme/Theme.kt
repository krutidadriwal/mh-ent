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
    primary = AmberGoldLight,
    onPrimary = Navy900,
    primaryContainer = AmberGoldDark,
    onPrimaryContainer = AmberGoldContainer,
    secondary = ElectricCyanLight,
    onSecondary = Navy900,
    secondaryContainer = Navy700,
    onSecondaryContainer = Color.White,
    tertiary = EmeraldGreenLight,
    onTertiary = Navy900,
    background = SlateDarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = SlateDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = SlateDarkCard,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = SlateDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Navy500,
    onPrimary = Color.White,
    primaryContainer = Navy100,
    onPrimaryContainer = Navy900,
    secondary = AmberGold,
    onSecondary = Color.White,
    secondaryContainer = AmberGoldContainer,
    onSecondaryContainer = AmberGoldDark,
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    background = SlateLightBackground,
    onBackground = Navy900,
    surface = SlateLightSurface,
    onSurface = Navy900,
    surfaceVariant = SlateLightBackground,
    onSurfaceVariant = Navy700,
    outline = SlateLightBorder
)

@Composable
fun MahalaxmiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) = MahalaxmiTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
