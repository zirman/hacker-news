package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.html.toHtmlAnnotatedString
import com.monoid.hackernews.common.view.itemdetail.htmlTextStyle
import com.monoid.hackernews.common.view.platform.PlatformLoadingIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HtmlPane(htmlString: String, modifier: Modifier = Modifier) {
    var htmlAnnotatedString: AnnotatedString? by remember {
        mutableStateOf(null)
    }
    LaunchedEffect(htmlString) {
        withContext(Dispatchers.Default) {
            htmlAnnotatedString = htmlString.toHtmlAnnotatedString()
        }
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val htmlAnnotatedStringPinned = htmlAnnotatedString
        if (htmlAnnotatedStringPinned != null) {
            Text(
                text = htmlAnnotatedStringPinned,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = 640.dp)
                    .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.End).asPaddingValues())
                    .padding(16.dp)
                    .fillMaxSize(),
                style = htmlTextStyle(),
            )
        } else {
            PlatformLoadingIndicator()
        }
    }
}
