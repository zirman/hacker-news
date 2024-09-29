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
    private val tokens: ArrayDeque<HtmlToken> = tokenizeHtml(htmlString)

    // tracks depth of indentation
    private val stack: ArrayDeque<HtmlToken.Tag> = ArrayDeque()

    @Suppress("LoopWithTooManyJumpStatements", "CyclomaticComplexMethod")
    fun parse(): AnnotatedString = buildAnnotatedString {
        var index = 0
        var appendedWord = false
        while (true) {
            if (index >= tokens.size) break
            when (val token = tokens[index]) {
                is HtmlToken.Tag -> {
                    if (token.isBlock()) {
                        if (token.isStart()) {
                            // push span styles up to index
                            pushBlock(index)
                            // pop all spans and block
                            repeat(stack.size) { pop() }
                            // drop block from stack
                            if (stack.firstOrNull()?.isBlock() == true) {
                                stack.removeFirst()
                            }
                            pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                            // push spans
                            stack.forEach { pushStyleForSpanTag(it) }
                            // save block
                            stack.addFirst(token)
                        } else if (stack.firstOrNull()?.isBlock() == true) {
                            // handle matched close tag
                            if (token.start.substring(2) == stack.first().start.substring(1)) {
                                // pop all styles
                                repeat(stack.size) { pop() }
                                stack.removeFirst()
                                stack.forEach { pushStyleForSpanTag(it) }
                                repeat(index) { tokens.removeFirst() }
                            } else {
                                // handle mismatched block
                                println("mismatch")
                            }
                        } else {
                            // handle unmatched close block
                            println("unmatched")
                        }
                        index = 0
                        appendedWord = false
                        tokens.removeFirst()
                        continue
                    }
                    index++
                }

                is HtmlToken.Word -> {
                    if (stack.firstOrNull()?.start == "<pre") {
                        if (appendedWord) {
                            appendWordPreformatted(index)
                        } else {
                            appendWordPreformattedStart(index)
                        }
                    } else if (appendedWord) {
                        appendWordWithSpace(index)
                    } else {
                        appendWord(index)
                    }
                    append(token.word)
                    appendedWord = true
                    tokens.removeFirst()
                    index = 0
                }

                is HtmlToken.Whitespace -> {
                    index++
                }
            }
        }
        // ignore spaces at the end
        repeat(stack.size) { pop() }
        stack.clear()
    }

    private fun AnnotatedString.Builder.pushStyleForSpanTag(tag: HtmlToken.Tag) {
        when (tag.start) {
            "<b" -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }

            "<i", "<cite", "<dfn", "<em" -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
            }

            "<big" -> {
                pushStyle(SpanStyle(fontSize = 1.25f.em))
            }

            "<small" -> {
                pushStyle(SpanStyle(fontSize = .8f.em))
            }

            "<tt", "<code" -> {
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

            else -> {
                check(false)
            }
        }
    }

    private fun AnnotatedString.Builder.appendWord(index: Int) {
        repeat(index) {
            when (val token = tokens.removeFirst()) {
                is HtmlToken.Tag -> {
                    spanTag(token)
                }

                is HtmlToken.Whitespace -> {
                }

                is HtmlToken.Word -> {
                    check(false)
                }
            }
        }
    }

    private fun AnnotatedString.Builder.appendWordWithSpace(index: Int) {
        var appendedSpace = false
        repeat(index) {
            when (val token = tokens.removeFirst()) {
                is HtmlToken.Tag -> {
                    spanTag(token)
                }

                is HtmlToken.Whitespace -> {
                    if (!appendedSpace) {
                        append(' ')
                        appendedSpace = true
                    }
                }

                is HtmlToken.Word -> {
                    check(false)
                }
            }
        }
    }

    private fun AnnotatedString.Builder.appendWordPreformattedStart(index: Int) {
        var x = true
        repeat(index) {
            when (val token = tokens.removeFirst()) {
                is HtmlToken.Tag -> {
                    spanTag(token)
                }

                is HtmlToken.Whitespace -> {
                    if (x) {
                        append(token.whitespace.removePrefix("\n"))
                        x = false
                    } else {
                        append(token.whitespace)
                    }
                }

                is HtmlToken.Word -> {
                    check(false)
                }
            }
        }
    }

    private fun AnnotatedString.Builder.appendWordPreformatted(index: Int) {
        repeat(index) {
            when (val token = tokens.removeFirst()) {
                is HtmlToken.Tag -> {
                    spanTag(token)
                }

                is HtmlToken.Whitespace -> {
                    append(token.whitespace)
                }

                is HtmlToken.Word -> {
                    check(false)
                }
            }
        }
    }

    private fun AnnotatedString.Builder.pushBlock(index: Int) {
        repeat(index) {
            when (val token = tokens.removeFirst()) {
                is HtmlToken.Tag -> {
                    spanTag(token)
                }

                is HtmlToken.Whitespace -> {
                    // ignored
                }

                is HtmlToken.Word -> {
                    check(false)
                }
            }
        }
    }

//    private fun AnnotatedString.Builder.appendW(index: Int) {
//        for (i in 0..<index) {
//            when (val token = tokens.removeFirst()) {
//                is HtmlToken.Tag -> {
//                    flushSpanTag(token)
//                }
//
//                is HtmlToken.Whitespace -> {
//                    append(token.whitespace)
//                }
//
//                else -> {
//                    /* no-op */
//                }
//            }
//        }
//    }

    private fun AnnotatedString.Builder.spanTag(token: HtmlToken.Tag) {
        if (token.isStart()) {
            pushStyleForSpanTag(token)
            stack.addLast(token)
        } else if (token.start.substring(2) == stack.lastOrNull()?.start?.substring(1)) {
            pop()
            stack.removeLast()
        } else {
            println("tag mismatch")
        }
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
    data class Whitespace(val whitespace: String) : HtmlToken
    data class Tag(val start: String, val tokens: List<TagToken>, val end: String) : HtmlToken
}

sealed interface TagToken {
    data object Equal : TagToken
    data class Word(val word: String) : TagToken
    data class Quote(val tag: String) : TagToken
}

@Suppress("CyclomaticComplexMethod")
fun tokenizeHtml(htmlString: String): ArrayDeque<HtmlToken> {
    val tokens = ArrayDeque<HtmlToken>()
    var i = 0
    @Suppress("LoopWithTooManyJumpStatements")
    outer@ while (i < htmlString.length) {
        var match = whitespaceRegex.matchAt(htmlString, i)
        if (match != null) {
            i = match.range.last + 1
            tokens.add(HtmlToken.Whitespace(match.value))
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
                    tokens.add(
                        HtmlToken.Tag(
                            start.lowercase(),
                            tagTokens,
                            tagMatch.value.lowercase()
                        )
                    )
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
            tokens.add(HtmlToken.Word(match.value.escapeCharacters()))
            continue
        }
        @Suppress("UseCheckOrError", "ThrowingExceptionsWithoutMessageOrCause")
        throw IllegalStateException("no matching regexes")
    }
    return tokens
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

private fun HtmlToken.Tag.isBlock(): Boolean = when (start) {
    "<p", "</p", "<pre", "</pre" -> true
    else -> false
}

private fun HtmlToken.Tag.isStart(): Boolean = !start.startsWith("</")
