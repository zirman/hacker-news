package com.monoid.hackernews.common.view.itemdetail

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun WebViewPane(url: String?, modifier: Modifier) {
    AndroidView(
        factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
        },
        update = {
            if (url != null) {
                it.loadUrl(url)
            } else {
                it.stopLoading()
            }
        },
        modifier = modifier,
    )
}
