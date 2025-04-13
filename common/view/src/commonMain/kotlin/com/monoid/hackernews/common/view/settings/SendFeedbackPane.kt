package com.monoid.hackernews.common.view.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SendFeedbackPane(modifier: Modifier = Modifier) {
    HtmlPane(
        """
            |<h1>Send Feedback</h1>
            |<a href="mailto:hn@ycombinator.com">hn@ycombinator.com</a>
            |<br><a href="mailto:monoidscorp+feedback@gmail.com">monoidscorp+feedback@gmail.com</a> 
        """.trimMargin(),
        modifier = modifier,
    )
}
