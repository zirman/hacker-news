package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.faq_content
import org.jetbrains.compose.resources.stringResource

@Composable
fun HelpPane(modifier: Modifier = Modifier) {
    HtmlPane(
        stringResource(Res.string.faq_content),
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    )
}
