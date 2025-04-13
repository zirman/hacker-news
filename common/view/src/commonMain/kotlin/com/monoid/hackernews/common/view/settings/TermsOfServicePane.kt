package com.monoid.hackernews.common.view.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TermsOfServicePane(modifier: Modifier = Modifier) {
    HtmlPane(
        """
            |<h1>Terms of Service</h1>
            |<a href="https://www.ycombinator.com/legal/">https://www.ycombinator.com/legal/</a>
        """.trimMargin(),
        modifier = modifier,
    )
}
