package com.monoid.hackernews.common.view.settings

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.guidelines_content
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserGuidelinesPane(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        HtmlPane(stringResource(Res.string.guidelines_content))
    }
}
