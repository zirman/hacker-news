package com.monoid.hackernews.common.view.settings

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.terms_of_service_content
import org.jetbrains.compose.resources.stringResource

@Composable
fun TermsOfServicePane(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        HtmlPane(stringResource(Res.string.terms_of_service_content))
    }
}
