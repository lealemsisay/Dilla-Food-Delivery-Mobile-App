package com.dilla.food.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dilla.food.data.*

@Composable
fun DillaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = PrimaryColor,
            onPrimary = Color.White,
            primaryContainer = PrimaryContainer,
            secondary = SecondaryColor,
            onSecondary = Color(0xFF6C3E00),
            secondaryContainer = SecondaryColor.copy(alpha = 0.2f),
            tertiary = TertiaryColor,
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFE8F5E9),
            background = SurfaceBg,
            onBackground = InkColor,
            surface = SurfaceBg,
            onSurface = InkColor,
            surfaceVariant = SurfaceContainer,
            onSurfaceVariant = GrayColor,
            outline = GrayColor,
            outlineVariant = OutlineVariant
        ),
        content = content
    )
}
