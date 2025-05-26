package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun listContentPadding(): PaddingValues {
    val density = LocalDensity.current
    return with(density) {
        val safeDrawing = WindowInsets.safeDrawing
        val layoutDirection = LocalLayoutDirection.current
        WindowInsets(
            top = safeDrawing.getTop(density),
            // TODO: ignore safe drawing bottom if using bottom nav
            bottom = safeDrawing.getBottom(density).coerceAtLeast(
                // height of fab
                80.dp.toPx().roundToInt()
            ),
            left = safeDrawing.getLeft(density, layoutDirection),
            right = safeDrawing.getRight(density, layoutDirection),
        )
    }.asPaddingValues()
}
