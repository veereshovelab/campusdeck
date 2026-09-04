package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GlassWhiteBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 16.dp, // Kept for API compatibility, but effect removed for sharpness
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            // Blur removed as it blurs children content, making it unreadable
            .background(GlassWhite)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(GlassWhiteBorder, Color.Transparent)
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

fun Modifier.glassEffect(
    cornerRadius: Dp = 24.dp,
    blurRadius: Dp = 16.dp // Kept for API compatibility, but effect removed for sharpness
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    // Blur removed as it blurs children content, making it unreadable
    .background(GlassWhite)
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(GlassWhiteBorder, Color.Transparent)
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
