package com.example.myvestidorapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val VividCoutureColorScheme = lightColorScheme(
    primary = ElectricPinkDark,
    onPrimary = VividSurface,
    primaryContainer = ElectricPinkLight,
    onPrimaryContainer = VividSurface,
    secondary = CyanSpark,
    onSecondary = VividSurface,
    tertiary = SoftAmethyst,
    background = VividBackground,
    onBackground = MidnightSlate,
    surface = VividSurface,
    onSurface = MidnightSlate,
    surfaceVariant = VividSurfaceLow,
    onSurfaceVariant = WarmSlate,
    outline = RoseIcon,
    outlineVariant = RoseOutline,
    error = ErrorRed,
)

@Composable
fun MyVestidorAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VividCoutureColorScheme,
        typography = Typography,
        content = content,
    )
}
