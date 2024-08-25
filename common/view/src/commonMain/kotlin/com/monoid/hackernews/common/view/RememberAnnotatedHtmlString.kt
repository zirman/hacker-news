package com.monoid.hackernews.common.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml

@Composable
fun rememberAnnotatedHtmlString(htmlString: String): AnnotatedString {
    val linkStyles = LocalLinkStyle.current
    return remember(htmlString, linkStyles) {
        AnnotatedString.fromHtml(
            htmlString = htmlString,
            linkStyles = linkStyles,
        )
    }
}
