package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BgColor,
    surface = SurfaceColor,
    surfaceVariant = SurfaceHighlightColor,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextColor,
    onSurface = TextColor,
    onSurfaceVariant = TextMutedColor,
    outline = BorderColor,
    outlineVariant = BorderColor,
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
