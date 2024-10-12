package com.monoid.hackernews.common.data.html

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections

private val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
private val italicStyle = SpanStyle(fontStyle = FontStyle.Italic)
private val bigStyle = SpanStyle(fontSize = 1.25f.em)
private val smallStyle = SpanStyle(fontSize = .8f.em)
private val monospaceStyle = SpanStyle(fontFamily = FontFamily.Monospace)
private val strikeStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)
private val underlineStyle = SpanStyle(textDecoration = TextDecoration.Underline)
private val superscriptStyle = SpanStyle(baselineShift = BaselineShift.Superscript)
private val subscriptStyle = SpanStyle(baselineShift = BaselineShift.Subscript)

class HtmlParser(
    val hStyles: List<SpanStyle> = listOf(
        SpanStyle(
            fontSize = 2.em,
            fontWeight = FontWeight.Bold,
        ),
        SpanStyle(
            fontSize = 1.5.em,
            fontWeight = FontWeight.Bold,
        ),
        SpanStyle(
            fontSize = 1.17.em,
            fontWeight = FontWeight.Bold,
        ),
        SpanStyle(
            fontWeight = FontWeight.Bold,
        ),
        SpanStyle(
            fontSize = 0.83.em,
            fontWeight = FontWeight.Bold,
        ),
        SpanStyle(
            fontSize = 0.67.em,
            fontWeight = FontWeight.Bold,
        ),
    ),
    val textLinkStyles: TextLinkStyles = TextLinkStyles(
        style = SpanStyle(),
        focusedStyle = SpanStyle(
            fontWeight = FontWeight.Bold,
        ),
        hoveredStyle = SpanStyle(
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
        ),
        pressedStyle = SpanStyle(
            fontWeight = FontWeight.ExtraBold,
            textDecoration = TextDecoration.Underline,
        ),
    )
) {
    private val pool = Collections.synchronizedList(mutableListOf<ParseState>())

    fun parse(htmlString: String): AnnotatedString {
        val parseState = pool.removeLastOrNull() ?: ParseState()
        val annotatedString = parseState.parse(htmlString)
        pool.add(parseState)
        return annotatedString
    }

    suspend fun parseParallel(htmlString: String): AnnotatedString = withContext(Dispatchers.Default) {
        parse(htmlString)
    }

    private inner class ParseState {
        private val stack: ArrayDeque<HtmlToken.Tag> = ArrayDeque()
        private var index: Int = 0
        private lateinit var tokens: ArrayDeque<HtmlToken>
        private lateinit var builder: AnnotatedString.Builder

        @Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "LoopWithTooManyJumpStatements")
        fun parse(htmlString: String): AnnotatedString {
            builder = AnnotatedString.Builder()
            tokens = tokenizeHtml(htmlString)
            var hasAppendedWord = false
            while (true) {
                if (index >= tokens.size) break
                when (val token = tokens[index]) {
                    is HtmlToken.Tag -> {
                        if (token.isBreak()) {
                            builder.appendLine()
                            index = 0
                            hasAppendedWord = false
                            tokens.removeFirst()
                            continue
                        } else if (token.isBlock()) {
                            if (token.isOpen()) {
                                // push span styles up to index
                                pushBlock()
                                // pop all spans and block
                                repeat(stack.size) { builder.pop() }
                                val firstTag = stack.firstOrNull()
                                // drop block from stack
                                if (firstTag?.isBlock() == true) {
                                    stack.removeFirst()
                                    if (firstTag.isHeader()) {
                                        builder.pop()
                                    }
                                }
                                pushParagraphStyle(token)
                                // push spans
                                stack.forEach { pushStyleForSpanTag(it) }
                                // save block
                                stack.addFirst(token)
                            } else if (stack.firstOrNull()?.isBlock() == true) {
                                // handle close tag
                                repeat(stack.size) { builder.pop() }
                                if (stack.first().isHeader()) {
                                    builder.pop()
                                }
                                if (token.start.substring(2) != stack.first().start.substring(1)) {
                                    // mismatched block
                                    pushParagraphStyle(token)
                                    builder.pop()
                                }
                                stack.removeFirst()
                                stack.forEach { pushStyleForSpanTag(it) }
                                repeat(index) { tokens.removeFirst() }
                            } else {
                                // unmatched close block
                                pushParagraphStyle(token)
                                builder.pop()
                            }
                            index = 0
                            hasAppendedWord = false
                            tokens.removeFirst()
                            continue
                        }
                        index++
                    }

                    is HtmlToken.Word -> {
                        if (stack.firstOrNull()?.isPreformatted() == true) {
                            if (hasAppendedWord) {
                                appendWordPreformatted()
                            } else {
                                appendWordPreformattedOpen()
                            }
                        } else if (hasAppendedWord) {
                            appendWordWithSpace()
                        } else {
                            appendWord()
                        }
                        builder.append(token.word)
                        hasAppendedWord = true
                        tokens.removeFirst()
                        index = 0
                    }

                    is HtmlToken.Whitespace -> {
                        index++
                    }
                }
            }
            stack.clear()
            return builder.toAnnotatedString()
        }

        private fun appendWord() {
            repeat(index) {
                when (val token = tokens.removeFirst()) {
                    is HtmlToken.Tag -> {
                        spanTag(token)
                    }

                    is HtmlToken.Whitespace -> {
                    }

                    is HtmlToken.Word -> {
                        throw IllegalStateException("Invalid tag")
                    }
                }
            }
        }

        private fun appendWordWithSpace() {
            var hasAppendedSpace = false
            repeat(index) {
                when (val token = tokens.removeFirst()) {
                    is HtmlToken.Tag -> {
                        spanTag(token)
                    }

                    is HtmlToken.Whitespace -> {
                        if (!hasAppendedSpace) {
                            builder.append(' ')
                            hasAppendedSpace = true
                        }
                    }

                    is HtmlToken.Word -> {
                        throw IllegalStateException("Invalid tag")
                    }
                }
            }
        }

        private fun appendWordPreformattedOpen() {
            var hasAppendedWhitespace = false
            repeat(index) {
                when (val token = tokens.removeFirst()) {
                    is HtmlToken.Tag -> {
                        spanTag(token)
                    }

                    is HtmlToken.Whitespace -> {
                        if (hasAppendedWhitespace) {
                            builder.append(token.whitespace)
                        } else {
                            builder.append(token.whitespace.removePrefix("\n"))
                            hasAppendedWhitespace = true
                        }
                    }

                    is HtmlToken.Word -> {
                        throw IllegalStateException("Invalid tag")
                    }
                }
            }
        }

        private fun appendWordPreformatted() {
            repeat(index) {
                when (val token = tokens.removeFirst()) {
                    is HtmlToken.Tag -> {
                        spanTag(token)
                    }

                    is HtmlToken.Whitespace -> {
                        builder.append(token.whitespace)
                    }

                    is HtmlToken.Word -> {
                        throw IllegalStateException("Invalid tag")
                    }
                }
            }
        }

        private fun pushBlock() {
            repeat(index) {
                when (val token = tokens.removeFirst()) {
                    is HtmlToken.Tag -> {
                        spanTag(token)
                    }

                    is HtmlToken.Whitespace -> {
                        // ignored
                    }

                    is HtmlToken.Word -> {
                        throw IllegalStateException("Invalid tag")
                    }
                }
            }
        }

        private fun spanTag(token: HtmlToken.Tag) {
            if (token.isOpen()) {
                pushStyleForSpanTag(token)
                stack.addLast(token)
            } else if (token.start.substring(2) == stack.lastOrNull()?.start?.substring(1)) {
                builder.pop()
                stack.removeLast()
            } else {
                println("tag mismatch")
            }
        }

        private fun pushParagraphStyle(tag: HtmlToken.Tag) {
            builder.pushStyle(
                ParagraphStyle(
                    lineBreak = when (tag.start) {
                        "<p", "</p" -> LineBreak.Paragraph
                        "<pre", "</pre" -> LineBreak.Unspecified // TODO: disable soft wrap when possible
                        "<h1", "</h1", "<h2", "</h2", "<h3", "</h3", "<h4", "</h4", "<h5", "</h5",
                        "<h6", "</h6" -> LineBreak.Heading

                        else -> throw IllegalStateException("Token doesn't have configured linebreak")
                    }
                ).applyAttributes(tag.tokens.toAttributes()),
            )
            if (tag.isHeader()) {
                builder.pushStyle(hStyles[tag.toLevel().coerceIn(hStyles.indices)])
            }
        }

        @Suppress("CyclomaticComplexMethod")
        private fun pushStyleForSpanTag(tag: HtmlToken.Tag) {
            when (tag.start) {
                "<b", "<strong" -> {
                    builder.pushStyle(boldStyle)
                }

                "<i", "<cite", "<dfn", "<em", "<address" -> {
                    builder.pushStyle(italicStyle)
                }

                "<big" -> {
                    builder.pushStyle(bigStyle)
                }

                "<small" -> {
                    builder.pushStyle(smallStyle)
                }

                "<tt", "<code" -> {
                    builder.pushStyle(monospaceStyle)
                }

                "<s", "<strike", "<del" -> {
                    builder.pushStyle(strikeStyle)
                }

                "<u" -> {
                    builder.pushStyle(underlineStyle)
                }

                "<sup" -> {
                    builder.pushStyle(superscriptStyle)
                }

                "<sub" -> {
                    builder.pushStyle(subscriptStyle)
                }

                "<font" -> {
                    builder.pushStyle(
                        tag.tokens.toAttributes()
                            ?.let { attributes ->
                                when (attributes.lookup("face")) {
                                    "monospace" -> SpanStyle(fontFamily = FontFamily.Monospace)
                                    "serif" -> SpanStyle(fontFamily = FontFamily.Serif)
                                    "sans_serif" -> SpanStyle(fontFamily = FontFamily.SansSerif)
                                    "cursive" -> SpanStyle(fontFamily = FontFamily.Cursive)
                                    else -> SpanStyle()
                                }.let { style ->
                                    val color = attributes.lookup("color")
                                    if (color != null) {
                                        // TODO: parse color
                                        style
                                    } else {
                                        style
                                    }
                                }
                            }
                            ?: SpanStyle(),
                    )
                }

                "<span" -> {
                    // CSS style: <span style=”color|background_color|text-decoration”>
                    builder.pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                }

                "<a" -> {
                    // randomly causes IndexOutOfBoundsException in MultiParagraph
                    builder.pushLink(
                        LinkAnnotation.Url(
                            url = tag.tokens.toAttributes()?.lookup("href") ?: "",
                            styles = textLinkStyles,
                        ),
                    )
                }

                else -> {
                    throw IllegalStateException("Invalid tag")
                }
            }
        }
    }
}
