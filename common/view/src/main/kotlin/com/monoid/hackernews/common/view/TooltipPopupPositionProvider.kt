package com.monoid.hackernews.common.view

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

@Immutable
class TooltipPopupPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        return IntOffset(
            x = ((anchorBounds.left + anchorBounds.right) / 2) - (popupContentSize.width / 2),
            y = anchorBounds.bottom,
        )
    }
}
