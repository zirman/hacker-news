package com.monoid.hackernews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun Scrim(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) {
        content()
        // draw scrim
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val systemBarsPadding = WindowInsets.statusBars.asPaddingValues(density)
        val background = MaterialTheme.colorScheme.background
        val colors = listOf(
            background,
            background.copy(alpha = .9f),
            background.copy(alpha = .8f),
            background.copy(alpha = 0f)
        )
        Canvas(
            modifier = Modifier.matchParentSize(),
            onDraw = {
                val topPadding = systemBarsPadding.calculateTopPadding().toPx()
                drawRect(
                    brush = Brush.verticalGradient(colors = colors, endY = topPadding),
                    size = size.copy(height = topPadding),
                    blendMode = BlendMode.SrcAtop,
                )
                val leftPadding = systemBarsPadding.calculateLeftPadding(layoutDirection).toPx()
                drawRect(
                    brush = Brush.horizontalGradient(colors = colors, endX = leftPadding),
                    size = size.copy(width = leftPadding),
                    blendMode = BlendMode.SrcAtop,
                )
                val bottomPadding = systemBarsPadding.calculateBottomPadding().toPx()
                val bottomPaddingOffset = size.height - bottomPadding
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = colors,
                        startY = size.height,
                        endY = bottomPaddingOffset,
                    ),
                    topLeft = Offset(
                        x = 0f,
                        y = bottomPaddingOffset,
                    ),
                    size = size.copy(height = bottomPadding),
                )
                val rightPadding = systemBarsPadding.calculateRightPadding(layoutDirection).toPx()
                val rightPaddingOffset = size.width - rightPadding
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = colors,
                        startX = size.width,
                        endX = rightPaddingOffset,
                    ),
                    topLeft = Offset(
                        x = rightPaddingOffset,
                        y = 0f,
                    ),
                    size = size.copy(width = rightPadding),
                )
            },
        )
    }
}
