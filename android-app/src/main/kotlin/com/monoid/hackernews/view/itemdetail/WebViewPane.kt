package com.monoid.hackernews.view.itemdetail

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewPane(url: String?, modifier: Modifier = Modifier) {
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
