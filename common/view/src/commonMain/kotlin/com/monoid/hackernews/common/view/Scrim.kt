package com.monoid.hackernews.common.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
            background.copy(alpha = 0f),
        )
        val topPadding = systemBarsPadding.calculateTopPadding()
        Canvas(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(topPadding),
            onDraw = {
                val topPaddingPx = topPadding.toPx()
                drawRect(
                    brush = Brush.verticalGradient(colors = colors, endY = topPaddingPx),
                    size = size.copy(height = topPaddingPx),
                    blendMode = BlendMode.SrcAtop,
                )
            },
        )
        val bottomPadding = systemBarsPadding.calculateBottomPadding()
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(topPadding),
            onDraw = {
                val bottomPaddingPx = bottomPadding.toPx()
                val bottomPaddingOffset = size.height - bottomPaddingPx
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
                    size = size.copy(height = bottomPaddingPx),
                )
            },
        )
        val leftPadding = systemBarsPadding.calculateLeftPadding(layoutDirection)
        Canvas(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .height(leftPadding),
            onDraw = {
                val leftPaddingPx = leftPadding.toPx()
                drawRect(
                    brush = Brush.horizontalGradient(colors = colors, endX = leftPaddingPx),
                    size = size.copy(width = leftPaddingPx),
                    blendMode = BlendMode.SrcAtop,
                )
            },
        )
        val rightPadding = systemBarsPadding.calculateRightPadding(layoutDirection)
        Canvas(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .height(rightPadding),
            onDraw = {
                val rightPaddingPx = rightPadding.toPx()
                val rightPaddingOffset = size.width - rightPaddingPx
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
                    size = size.copy(width = rightPaddingPx),
                )
            },
        )
    }
}
