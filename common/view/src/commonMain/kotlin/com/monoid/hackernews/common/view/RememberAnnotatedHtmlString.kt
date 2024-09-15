package com.monoid.hackernews.common.view

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit

@Suppress("CyclomaticComplexMethod")
@Composable
fun rememberAnnotatedHtmlString(htmlString: String): AnnotatedString {
    val linkStyle = LocalLinkStyle.current.style
    val fontSize = LocalTextStyle.current.fontSize
    return remember(htmlString, linkStyle, fontSize) {
        annotateHtmlString(htmlString, linkStyle, fontSize)
    }
}

class HtmlParser(
    htmlString: String,
    private val linkStyle: SpanStyle?,
    private val textUnit: TextUnit,
    private val tokens: List<HtmlToken> = tokenizeHtml(htmlString),
    // newLine and consumedSpace are exclusively true or both are false
    private var newLine: Boolean = true,
    private var consumedSpace: Boolean = false,
    private var ulDepth: Int = 0,
    private val tagStack: ArrayDeque<HtmlToken.Tag> = ArrayDeque(),
    private val tagQueue: ArrayDeque<HtmlToken.Tag> = ArrayDeque(),
) {
    fun parse(): AnnotatedString = buildAnnotatedString {
        var i = 0
        while (true) {
            if (i >= tokens.size) {
                break
            }
            when (val token = tokens[i]) {
                HtmlToken.Whitespace -> {
                    // ignore whitespace when on a new line
                    if (!newLine) {
                        consumedSpace = true
                    }
                }

                is HtmlToken.Word -> {
                    if (consumedSpace) {
                        append(' ')
                        flushTagQueue()
                    }
                    append(token.word)
                    newLine = false
                    consumedSpace = false
                }

                is HtmlToken.Tag -> {
                    // immediately apply style tokens or format tokens
                    if (newLine || !consumedSpace) {
                        if (token.start.startsWith("</")) {
                            handleCloseTagImmediate(token)
                        } else {
                            handleOpenTagImmediate(token)
                        }
                    } else if (token.start.startsWith("</")) {
                        handleCloseTagQueued(token)
                    } else {
                        handleOpenTagQueued(token)
                    }
                }
            }
            i++
        }
        flushTagQueue()
    }

    @Suppress("CyclomaticComplexMethod")
    private fun AnnotatedString.Builder.handleCloseTagImmediate(token: HtmlToken.Tag) {
        if (token.start == "</br") {
            appendNewLine()
            return
        }
        val t = tagStack.lastOrNull()
        if (token.start.substring(2) != t?.start?.substring(1)) {
            println("mismatch")
            return
        }
        tagStack.removeLast()
        when (token.start) {
            "</b", "</i", "</cite", "</dfn", "</em", "</big", "</small", "</tt", "</s", "</strike", "</del", "</u",
            "</sup", "</sub", "</font", "</a" -> {
                pop()
            }

            "</ul" -> {
                ulDepth--
                appendLineBreak()
            }

            "</li" -> {
                appendLineBreak()
            }

            "</p" -> {
                appendLineBreak()
            }

            "</div" -> {
            }

            else -> {
            }
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private fun AnnotatedString.Builder.handleOpenTagImmediate(token: HtmlToken.Tag) {
        // unpaired tag
        if (token.start == "<br") {
            appendNewLine()
            return
        }
        tagStack.addLast(token)
        when (token.start) {
            "<b" -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }

            "<i", "<cite", "<dfn", "<em" -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
            }

            "<big" -> {
                pushStyle(SpanStyle(fontSize = textUnit * 1.25))
            }

            "<small" -> {
                pushStyle(SpanStyle(fontSize = textUnit * .8))
            }

            "<tt" -> {
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
            }

            "<s", "<strike", "<del" -> {
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
            }

            "<u" -> {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }

            "<sup" -> {
                pushStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
            }

            "<sub" -> {
                pushStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
            }

            "<font" -> {
                // todo: apply font
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }

            "<a" -> {
                // todo: apply link
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }

            "<ul" -> {
                ulDepth++
            }

            "<li" -> {
                appendLineBreak()
                repeat(ulDepth) {
                    append("  ")
                }
                append("* ")
            }

            "<p" -> {
                appendLineBreak()
            }

            "<div" -> {
            }

            else -> {
            }
        }
    }

    private fun AnnotatedString.Builder.handleCloseTagQueued(htmlTag: HtmlToken.Tag) {
        // unpaired tag
        if (htmlTag.start == "</br") {
            appendNewLine()
            return
        }
        // find open tag in stack
        val t = tagStack.lastOrNull()
        if (t == null || htmlTag.start.substring(2) != t.start.substring(1)) {
            println("mismatch")
        }
        when (htmlTag.start) {
            // style tokens are queued until after whitespace is produced
            "</b", "</i", "</cite", "</dfn", "</em", "</big", "</small", "</tt", "</s", "</strike", "</del", "</u",
            "</sup", "</sub", "</font", "</a" -> {
                tagQueue.add(htmlTag)
            }

            "</ul" -> {
                ulDepth--
            }

            "</li" -> {
                appendLineBreak()
            }

            "</p" -> {
                appendLineBreak()
            }

            "</div" -> {
            }

            // unknown tag
            else -> {
            }
        }
    }

    private fun AnnotatedString.Builder.handleOpenTagQueued(htmlTag: HtmlToken.Tag) {
        // unpaired tag
        if (htmlTag.start == "<br") {
            appendNewLine()
            return
        }
        when (htmlTag.start) {
            // style tokens are queued until after whitespace is produced
            "<b", "<i", "<cite", "<dfn", "<em", "<big", "<small", "<tt", "<s", "<strike", "<del", "<u", "<sup", "<sub",
            "<font", "<a" -> {
                tagQueue.add(htmlTag)
            }

            "<ul" -> {
                ulDepth++
            }

            "<li" -> {
                appendLineBreak()
                repeat(ulDepth) {
                    append("  ")
                }
                append("* ")
                tagStack.addLast(htmlTag)
            }

            "<p" -> {
                appendLineBreak()
                tagStack.addLast(htmlTag)
            }

            "<div" -> {
                tagStack.addLast(htmlTag)
            }

            // unknown tag
            else -> {
            }
        }
    }

    private fun AnnotatedString.Builder.appendLineBreak() {
        if (!newLine) {
            appendNewLine()
        }
    }

    private fun AnnotatedString.Builder.appendNewLine() {
        flushTagQueue()
        appendLine()
        newLine = true
        consumedSpace = false
    }

    private fun AnnotatedString.Builder.flushTagQueue() {
        while (true) {
            val tag = tagQueue.removeFirstOrNull() ?: break
            if (tag.start.startsWith("</")) {
                handleCloseTagImmediate(tag)
            } else {
                handleOpenTagImmediate(tag)
            }
        }
    }
}

// TODO
// CSS style: <span style=”color|background_color|text-decoration”>
fun annotateHtmlString(
    htmlString: String,
    linkStyle: SpanStyle?,
    fontSize: TextUnit,
): AnnotatedString = HtmlParser(htmlString, linkStyle, fontSize).parse()

// Hacker News Formatting
// Blank lines separate paragraphs.
// Text surrounded by asterisks is italicized. To get a literal asterisk, use \* or **.
// Text after a blank line that is indented by two or more spaces is reproduced verbatim. (This is intended for code.)
// Urls become links, except in the text field of a submission.
// If your url gets linked incorrectly, put it in <angle brackets> and it should work.

private val whitespaceRegex = """\s+""".toRegex(RegexOption.IGNORE_CASE)
private val tagStartRegex = """</?[^\s>]+""".toRegex(RegexOption.IGNORE_CASE)
private val tagWordRegex = """[^="\s>]+""".toRegex(RegexOption.IGNORE_CASE)
private val tagQuoteRegex = """"([^"]*)"""".toRegex(RegexOption.IGNORE_CASE)
private val tagEqualRegex = """=""".toRegex(RegexOption.IGNORE_CASE)
private val tagEndRegex = """/?>""".toRegex(RegexOption.IGNORE_CASE)
private val wordRegex = """[^<\s]+""".toRegex(RegexOption.IGNORE_CASE)

sealed interface HtmlToken {
    data class Word(val word: String) : HtmlToken
    data object Whitespace : HtmlToken
    data class Tag(val start: String, val tokens: List<TagToken>, val end: String) : HtmlToken
}

sealed interface TagToken {
    data object Equal : TagToken
    data class Word(val word: String) : TagToken
    data class Quote(val tag: String) : TagToken
}

@Suppress("CyclomaticComplexMethod")
fun tokenizeHtml(htmlString: String): List<HtmlToken> = buildList {
    var i = 0
    @Suppress("LoopWithTooManyJumpStatements")
    outer@ while (i < htmlString.length) {
        var match = whitespaceRegex.matchAt(htmlString, i)
        if (match != null) {
            i = match.range.last + 1
            add(HtmlToken.Whitespace)
            continue
        }
        match = tagStartRegex.matchAt(htmlString, i)
        if (match != null) {
            var k = match.range.last + 1
            val start = match.value
            val tagTokens = mutableListOf<TagToken>()
            while (k < htmlString.length) {
                var tagMatch = whitespaceRegex.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    continue
                }
                tagMatch = tagEndRegex.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagMatch.value.replace("&amp;", "&")
                    add(HtmlToken.Tag(start.lowercase(), tagTokens, tagMatch.value.lowercase()))
                    i = k
                    continue@outer
                }
                tagMatch = tagEqualRegex.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagTokens.add(TagToken.Equal)
                    continue
                }
                tagMatch = tagQuoteRegex.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagTokens.add(TagToken.Quote(tagMatch.groups[1]!!.value.escapeCharacters()))
                    continue
                }
                tagMatch = tagWordRegex.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagTokens.add(TagToken.Word(tagMatch.value))
                    continue
                }
            }
            // end of input found before end of tag so fall through
        }
        match = wordRegex.matchAt(htmlString, i)
        if (match != null) {
            i = match.range.last + 1
            add(HtmlToken.Word(match.value.escapeCharacters()))
            continue
        }
        @Suppress("UseCheckOrError", "ThrowingExceptionsWithoutMessageOrCause")
        throw IllegalStateException("no matching regexes")
    }
}

private val escapedRegex = """&([^;]+);""".toRegex(RegexOption.IGNORE_CASE)

private fun String.escapeCharacters(): String = buildString {
    var match = escapedRegex.find(this@escapeCharacters, 0)
    var i = 0
    while (match != null) {
        append(this@escapeCharacters.subSequence(i, match.range.first))
        val m = match.groups[1]!!.value
        when {
            m.equals("amp", ignoreCase = true) -> {
                append('&')
            }

            m.equals("lt", ignoreCase = true) -> {
                append('<')
            }

            m.equals("gt", ignoreCase = true) -> {
                append('>')
            }

            m.equals("quot", ignoreCase = true) -> {
                append('"')
            }

            m.startsWith("#") -> {
                val c = m.substring(1).toIntOrNull()?.toChar()
                if (c != null) {
                    append(c)
                } else {
                    append(match.value)
                }
            }

            else -> {
                append(match.value)
            }
        }
        i = match.range.last + 1
        match = match.next()
    }
    if (i < this@escapeCharacters.length) {
        append(this@escapeCharacters.subSequence(i, this@escapeCharacters.length))
    }
}
