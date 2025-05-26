package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.about_us_detail_html
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutPane(modifier: Modifier = Modifier) {
    HtmlPane(
        stringResource(Res.string.about_us_detail_html),
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    )
}
