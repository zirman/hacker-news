package com.monoid.hackernews.common.view.html

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

    @Suppress("LoopWithTooManyJumpStatements", "CyclomaticComplexMethod")
    fun parse(htmlString: String): AnnotatedString {
        return ParseState().parse(htmlString)
    }

    private fun AnnotatedString.Builder.pushParagraphStyle(tag: HtmlToken.Tag) {
        pushStyle(
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
            pushStyle(hStyles[tag.toLevel().coerceIn(hStyles.indices)])
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private fun AnnotatedString.Builder.pushStyleForSpanTag(tag: HtmlToken.Tag) {
        when (tag.start) {
            "<b", "<strong" -> {
                pushStyle(boldStyle)
            }

            "<i", "<cite", "<dfn", "<em", "<address" -> {
                pushStyle(italicStyle)
            }

            "<big" -> {
                pushStyle(bigStyle)
            }

            "<small" -> {
                pushStyle(smallStyle)
            }

            "<tt", "<code" -> {
                pushStyle(monospaceStyle)
            }

            "<s", "<strike", "<del" -> {
                pushStyle(strikeStyle)
            }

            "<u" -> {
                pushStyle(underlineStyle)
            }

            "<sup" -> {
                pushStyle(superscriptStyle)
            }

            "<sub" -> {
                pushStyle(subscriptStyle)
            }

            "<font" -> {
                pushStyle(
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
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
            }

            "<a" -> {
                // randomly causes IndexOutOfBoundsException in MultiParagraph
                pushLink(
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
                                builder.pushParagraphStyle(token)
                                // push spans
                                stack.forEach { builder.pushStyleForSpanTag(it) }
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
                                    builder.pushParagraphStyle(token)
                                    builder.pop()
                                }
                                stack.removeFirst()
                                stack.forEach { builder.pushStyleForSpanTag(it) }
                                repeat(index) { tokens.removeFirst() }
                            } else {
                                // unmatched close block
                                builder.pushParagraphStyle(token)
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
                builder.pushStyleForSpanTag(token)
                stack.addLast(token)
            } else if (token.start.substring(2) == stack.lastOrNull()?.start?.substring(1)) {
                builder.pop()
                stack.removeLast()
            } else {
                println("tag mismatch")
            }
        }
    }

    companion object {
        private val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
        private val italicStyle = SpanStyle(fontStyle = FontStyle.Italic)
        private val bigStyle = SpanStyle(fontSize = 1.25f.em)
        private val smallStyle = SpanStyle(fontSize = .8f.em)
        private val monospaceStyle = SpanStyle(fontFamily = FontFamily.Monospace)
        private val strikeStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)
        private val underlineStyle = SpanStyle(textDecoration = TextDecoration.Underline)
        private val superscriptStyle = SpanStyle(baselineShift = BaselineShift.Superscript)
        private val subscriptStyle = SpanStyle(baselineShift = BaselineShift.Subscript)
    }
}
//        val stack: ArrayDeque<HtmlToken.Tag> = ArrayDeque()
//        val tokens: ArrayDeque<HtmlToken> = tokenizeHtml(htmlString)
