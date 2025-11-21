package com.example.xiaoyu_angelica_comp304sec001_lab04.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Indigo,
    onPrimary = OnPrimary,
    surface = Surface,
    onSurface = OnSurface,
    background = SakuraPink
)

private val DarkColors = darkColorScheme(
    primary = Indigo,
    onPrimary = OnPrimary,
    surface = CardBackground,
    onSurface = OnSurface,
    background = Color(0xFF111219)
)

@Composable
fun OsakaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!darkTheme) LightColors else DarkColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}