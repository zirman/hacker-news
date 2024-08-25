package com.monoid.hackernews.common.view

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration

/**
 * CompositionLocal containing the preferred [LocalLinkStyle] that will be used by [rememberAnnotatedHtmlString].
 */
@Suppress("ComposeCompositionLocalUsage")
val LocalLinkStyle = compositionLocalOf(structuralEqualityPolicy()) {
    TextLinkStyles(
        style = SpanStyle(
            textDecoration = TextDecoration.Underline,
        ),
    )
}
