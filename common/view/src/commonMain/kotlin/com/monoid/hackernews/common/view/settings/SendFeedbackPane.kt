package com.monoid.hackernews.common.view.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.send_feedback_content
import org.jetbrains.compose.resources.stringResource

@Composable
fun SendFeedbackPane(modifier: Modifier = Modifier) {
    HtmlPane(
        stringResource(Res.string.send_feedback_content),
        modifier = modifier,
    )
}
