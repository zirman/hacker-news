package com.monoid.hackernews.common.view.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.faq
import org.jetbrains.compose.resources.stringResource

@Composable
fun HelpPane(modifier: Modifier = Modifier) {
    HtmlPane(stringResource(Res.string.faq), modifier = modifier)
}
