package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SportGold,
    secondary = SportOrange,
    tertiary = SportBlue,
    background = SportDarkBg,
    surface = SportDarkSurface,
    onPrimary = TextDarkOnGold,
    onSecondary = TextCrispWhite,
    onTertiary = TextCrispWhite,
    onBackground = TextCrispWhite,
    onSurface = TextCrispWhite
)

private val LightColorScheme = lightColorScheme(
    primary = SportGold,
    secondary = SportOrange,
    tertiary = SportBlue,
    background = SportDarkBg,
    surface = SportDarkSurface,
    onPrimary = TextDarkOnGold,
    onSecondary = TextCrispWhite,
    onTertiary = TextCrispWhite,
    onBackground = TextCrispWhite,
    onSurface = TextCrispWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Sport Theme by default
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
