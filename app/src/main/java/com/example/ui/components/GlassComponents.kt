package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoLavenderContent
import com.example.ui.theme.BentoLilacContainer
import com.example.ui.theme.BentoLilacContent
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GlassWhiteBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 16.dp,
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
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
    blurRadius: Dp = 16.dp
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(GlassWhite)
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(GlassWhiteBorder, Color.Transparent)
        ),
        shape = RoundedCornerShape(cornerRadius)
    )

/**
 * 🌟 DANGER GLOW CARD: Rotating sweep gradient border card with spring interaction!
 */
@Composable
fun GlowingBorderCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "border_glow")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    val glowBrush = Brush.sweepGradient(
        colors = listOf(
            BentoLavenderContent,
            BentoLilacContent,
            Color(0xFFEC4899),
            Color(0xFF3B82F6),
            BentoLavenderContent
        )
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White)
            .border(
                width = 2.dp,
                brush = glowBrush,
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

/**
 * 💥 ANIMATED LIKE BUTTON: Spring bounce explosion when liked!
 */
@Composable
fun AnimatedLikeButton(
    hasLiked: Boolean,
    onLikeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (hasLiked) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "like_scale"
    )

    IconButton(
        onClick = onLikeToggle,
        modifier = modifier.scale(scale)
    ) {
        Icon(
            imageVector = if (hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Like Button",
            tint = if (hasLiked) Color(0xFFEF4444) else Color(0xFF94A3B8),
            modifier = Modifier.size(22.dp)
        )
    }
}
