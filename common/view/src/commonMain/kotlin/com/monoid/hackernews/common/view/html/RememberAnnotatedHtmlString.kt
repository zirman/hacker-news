package com.monoid.hackernews.common.view.html

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.monoid.hackernews.common.data.html.HtmlParser

private val htmlParser = HtmlParser()

@Composable
fun rememberAnnotatedHtmlString(htmlString: String): AnnotatedString = htmlParser.parse(htmlString)
