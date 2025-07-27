package com.monoid.hackernews.common.data.html

sealed interface HtmlToken {
    data class Word(val word: String) : HtmlToken
    data class Whitespace(val whitespace: String) : HtmlToken
    data class Tag(val start: String, val tokens: List<TagToken>, val end: String) : HtmlToken
}

sealed interface TagToken {
    data object Equal : TagToken
    data class Word(val word: String) : TagToken
    data class Quote(val tag: String) : TagToken
}

internal fun HtmlToken.Tag.isBreak(): Boolean = when (start) {
    "<br", "</br" -> true
    else -> false
}

internal fun HtmlToken.Tag.isBlock(): Boolean = when (start) {
    "<p", "</p", "<pre", "</pre", "<h1", "</h1", "<h2", "</h2", "<h3", "</h3", "<h4", "</h4", "<h5", "</h5",
    "<h6", "</h6", "<div", "</div"
    -> true

    else -> false
}

internal fun HtmlToken.Tag.isOpen(): Boolean = !start.startsWith("</")

internal fun HtmlToken.Tag.isPreformatted(): Boolean = start == "<pre" || start == "/pre"
internal fun HtmlToken.Tag.isHeader(): Boolean = start.startsWith("<h") || start.startsWith("</h")
internal fun HtmlToken.Tag.toLevel(): Int = if (start.startsWith("</")) {
    start[3]
} else {
    start[2]
} - '1'
