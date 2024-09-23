package com.monoid.hackernews.common.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em

@Suppress("CyclomaticComplexMethod")
@Composable
fun rememberAnnotatedHtmlString(htmlString: String): AnnotatedString {
    val linkStyle = LocalLinkStyle.current.style ?: SpanStyle()
    return remember(htmlString, linkStyle) {
        annotateHtmlString(htmlString, linkStyle)
    }
}

class HtmlParser(
    htmlString: String,
    private val linkStyle: SpanStyle,
) {
    private val tokens: List<HtmlToken> = tokenizeHtml(htmlString)

    // newLine and consumedSpace are exclusively true or both are false
    private var newLine: Boolean = true
    private var consumedSpace: Int = -1

    // tracks depth of indentation
    private var ulDepth: Int = 0
    private val tagStack: ArrayDeque<HtmlToken.Tag> = ArrayDeque()

    // style tags are queued until there is a word token to determine if if it is applied to whitespace
    private val queue: ArrayDeque<HtmlToken.Tag> = ArrayDeque()

    private var paragraphDepth = false

    fun parse(): AnnotatedString = buildAnnotatedString {
        var i = 0
        while (i < tokens.size) {
            when (val token = tokens[i]) {
                HtmlToken.Whitespace -> {
                    // ignore whitespace when on a new line
                    if (!newLine && consumedSpace == -1) {
                        consumedSpace = queue.size
                    }
                }

                is HtmlToken.Word -> {
                    flushQueue()
                    append(token.word)
                    newLine = false
                }

                is HtmlToken.Tag -> {
                    if (token.start == "<br" || token.start == "</br") {
                        appendNewLine()
                    } else if (token.start.startsWith("</")) {
                        queueCloseTag(token)
                    } else {
                        queueOpenTag(token)
                    }
                }
            }
            i++
        }
        // ignore spaces at the end
        consumedSpace = -1
        flushQueue()
    }

    @Suppress("CyclomaticComplexMethod")
    private fun AnnotatedString.Builder.handleCloseTagImmediate(tag: HtmlToken.Tag) {
        val t = tagStack.lastOrNull()
        if (tag.start.substring(2) != t?.start?.substring(1)) {
            //println("mismatch")
            return
        }
        tagStack.removeLast()
        when (tag.start) {
            "</b", "</i", "</cite", "</dfn", "</em", "</big", "</small", "</tt", "</s", "</strike", "</del", "</u",
            "</sup", "</sub", "</font", "</span", "</a" -> {
                pop()
            }

            "</p" -> {
                if (paragraphDepth) {
                    pop()
                    paragraphDepth = false
                }
            }

//            "</ul" -> {
//                ulDepth--
//                appendLineBreak()
//            }
//
//            "</li" -> {
//                appendLineBreak()
//            }

            else -> {
            }
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private fun AnnotatedString.Builder.handleOpenTagImmediate(tag: HtmlToken.Tag) {
        when (tag.start) {
            "<b" -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }

            "<i", "<cite", "<dfn", "<em" -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
            }

            "<big" -> {
                TextUnit.Unspecified
                pushStyle(SpanStyle(fontSize = 1.25f.em))
            }

            "<small" -> {
                pushStyle(SpanStyle(fontSize = .8f.em))
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
                // monospace, serif, and sans_serif
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }

            "<span" -> {
                // CSS style: <span style=”color|background_color|text-decoration”>
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }

            "<a" -> {
                pushStyle(linkStyle)
                // todo: apply link
            }

            "<p" -> {
                if (paragraphDepth) {
                    val i = tagStack.indexOfLast { it.start == "<p" }
                    for (k in i..<tagStack.size) {
                        pop()
                    }
                    tagStack.removeAt(tagStack.indexOfLast { it.start == "<p" })
                    for (k in i..<tagStack.size) {
                        handleOpenTagImmediate(tagStack[k])
                    }
                }
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                    ),
                )
                paragraphDepth = true
            }

//            "<ul" -> {
//                ulDepth++
//            }
//
//            "<li" -> {
//                appendLineBreak()
//                repeat(ulDepth) {
//                    append("  ")
//                }
//                append("* ")
//            }

            else -> {
            }
        }
        tagStack.addLast(tag)
    }

    private fun queueOpenTag(tag: HtmlToken.Tag) {
        // handle implied closure of tags
        if (tag.start == "<p") {
            val index = queue.indexOfLast { it.start == "<p" }
            if (index != -1) {
                queue.removeAt(index)
            }
        }
        queue.add(tag)
    }

    private fun queueCloseTag(tag: HtmlToken.Tag) {
        val tagName = tag.start.substring(2)
        // removed tags that were closed before being applied to a word
        val index = queue.indexOfLast { tagName == it.start.substring(1) }
        if (index != -1) {
            queue.removeAt(index)
        } else {
            queue.add(tag)
        }
    }

    private fun AnnotatedString.Builder.appendNewLine() {
        flushQueue()
        appendLine()
        newLine = true
    }

    private fun AnnotatedString.Builder.flushQueue() {
        queue.forEachIndexed { index, tag ->
            if (index == consumedSpace) {
                append(' ')
            }
            if (tag.start.startsWith("</")) {
                handleCloseTagImmediate(tag)
            } else {
                handleOpenTagImmediate(tag)
            }
        }
        if (consumedSpace == queue.size) {
            append(' ')
        }
        queue.clear()
        consumedSpace = -1
    }
}

// TODO
// CSS style: <span style=”color|background_color|text-decoration”>
fun annotateHtmlString(
    htmlString: String,
    linkStyle: SpanStyle,
): AnnotatedString = HtmlParser(htmlString, linkStyle).parse()

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
    var i = 0
    var match = escapedRegex.find(this@escapeCharacters, i)
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
