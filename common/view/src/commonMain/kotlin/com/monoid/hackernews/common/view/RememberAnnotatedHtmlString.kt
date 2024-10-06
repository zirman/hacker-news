package com.monoid.hackernews.common.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Suppress("CyclomaticComplexMethod")
@Composable
fun rememberAnnotatedHtmlString(htmlString: String): AnnotatedString {
    val linkStyle = LocalLinkStyle.current.style ?: SpanStyle()
    val typography = MaterialTheme.typography
    return remember(htmlString, linkStyle, typography) {
        annotateHtmlString(
            htmlString = htmlString,
            typography = typography,
            linkStyle = linkStyle,
        )
    }
}

class HtmlParser(
    htmlString: String,
    typography: Typography,
    linkStyle: SpanStyle,
) {
    private val hStyle = listOf(
        typography.headlineLarge.toSpanStyle(),
        typography.headlineMedium.toSpanStyle(),
        typography.headlineSmall.toSpanStyle(),
        typography.titleLarge.toSpanStyle(),
        typography.titleMedium.toSpanStyle(),
        typography.titleSmall.toSpanStyle()
    )
    private val textLinkStyles: TextLinkStyles = linkStyle.toTextLinkStyles()
    private val tokens: ArrayDeque<HtmlToken> = tokenizeHtml(htmlString)

    // tracks depth of indentation
    private val stack: ArrayDeque<HtmlToken.Tag> = ArrayDeque()

    @Suppress("LoopWithTooManyJumpStatements", "CyclomaticComplexMethod")
    fun parse(): AnnotatedString = buildAnnotatedString {
        var index = 0
        var hasAppendedWord = false
        while (true) {
            if (index >= tokens.size) break
            when (val token = tokens[index]) {
                is HtmlToken.Tag -> {
                    if (token.isBreak()) {
                        appendLine()
                        index = 0
                        hasAppendedWord = false
                        tokens.removeFirst()
                        continue
                    } else if (token.isBlock()) {
                        if (token.isOpen()) {
                            // push span styles up to index
                            pushBlock(index)
                            // pop all spans and block
                            repeat(stack.size) { pop() }
                            val firstTag = stack.firstOrNull()
                            // drop block from stack
                            if (firstTag?.isBlock() == true) {
                                stack.removeFirst()
                                if (firstTag.isHeader()) {
                                    pop()
                                }
                            }
                            pushParagraphStyle(token)
                            // push spans
                            stack.forEach { pushStyleForSpanTag(it) }
                            // save block
                            stack.addFirst(token)
                        } else if (stack.firstOrNull()?.isBlock() == true) {
                            // handle close tag
                            repeat(stack.size) { pop() }
                            if (stack.first().isHeader()) {
                                pop()
                            }
                            if (token.start.substring(2) != stack.first().start.substring(1)) {
                                // mismatched block
                                pushParagraphStyle(token)
                                pop()
                            }
                            stack.removeFirst()
                            stack.forEach { pushStyleForSpanTag(it) }
                            repeat(index) { tokens.removeFirst() }
                        } else {
                            // unmatched close block
                            pushParagraphStyle(token)
                            pop()
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
                            appendWordPreformatted(index)
                        } else {
                            appendWordPreformattedOpen(index)
                        }
                    } else if (hasAppendedWord) {
                        appendWordWithSpace(index)
                    } else {
                        appendWord(index)
                    }
                    append(token.word)
                    hasAppendedWord = true
                    tokens.removeFirst()
                    index = 0
                }

                is HtmlToken.Whitespace -> {
                    index++
                }
            }
        }
        // ignore whitespace at the end
        repeat(stack.size) { pop() }
        stack.clear()
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
            pushStyle(hStyle[tag.toLevel()])
        }
    }

    // TODO:
    // <mark // highlight
    // <q // inline quotation
    // <wbr // possible line break
    // <time
    // <blockquote
    // <ul
    // <ol
    // <dl
    // <span style=”color|background_color|text-decoration”>
    // text-shadow: 1px 1px 2px black;

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

    private fun AnnotatedString.Builder.appendWord(index: Int) {
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

    private fun AnnotatedString.Builder.appendWordWithSpace(index: Int) {
        var hasAppendedSpace = false
        repeat(index) {
            when (val token = tokens.removeFirst()) {
                is HtmlToken.Tag -> {
                    spanTag(token)
                }

                is HtmlToken.Whitespace -> {
                    if (!hasAppendedSpace) {
                        append(' ')
                        hasAppendedSpace = true
                    }
                }

                is HtmlToken.Word -> {
                    throw IllegalStateException("Invalid tag")
                }
            }
        }
    }

    private fun AnnotatedString.Builder.appendWordPreformattedOpen(index: Int) {
        var hasAppendedWhitespace = false
        repeat(index) {
            when (val token = tokens.removeFirst()) {
                is HtmlToken.Tag -> {
                    spanTag(token)
                }

                is HtmlToken.Whitespace -> {
                    if (hasAppendedWhitespace) {
                        append(token.whitespace)
                    } else {
                        append(token.whitespace.removePrefix("\n"))
                        hasAppendedWhitespace = true
                    }
                }

                is HtmlToken.Word -> {
                    throw IllegalStateException("Invalid tag")
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
                    throw IllegalStateException("Invalid tag")
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
                    throw IllegalStateException("Invalid tag")
                }
            }
        }
    }

    private fun AnnotatedString.Builder.spanTag(token: HtmlToken.Tag) {
        if (token.isOpen()) {
            pushStyleForSpanTag(token)
            stack.addLast(token)
        } else if (token.start.substring(2) == stack.lastOrNull()?.start?.substring(1)) {
            pop()
            stack.removeLast()
        } else {
            println("tag mismatch")
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

fun annotateHtmlString(
    htmlString: String,
    typography: Typography,
    linkStyle: SpanStyle,
): AnnotatedString = HtmlParser(htmlString, typography, linkStyle).parse()

// Hacker News Formatting
// Blank lines separate paragraphs.
// Text surrounded by asterisks is italicized. To get a literal asterisk, use \* or **.
// Text after a blank line that is indented by two or more spaces is reproduced verbatim. (This is intended for code.)
// Urls become links, except in the text field of a submission.
// If your url gets linked incorrectly, put it in <angle brackets> and it should work.

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

internal val WHITESPACE_REGEX = """\s+""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_START_REGEX = """</?[^\s/>]+""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_WORD_REGEX = """[^="\s>]+""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_QUOTE_REGEX = """"([^"]*)"""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_EQUAL_REGEX = """=""".toRegex(RegexOption.IGNORE_CASE)
internal val TAG_END_REGEX = """/?>""".toRegex(RegexOption.IGNORE_CASE)
internal val WORD_REGEX = """[^<\s]+""".toRegex(RegexOption.IGNORE_CASE)

@Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
fun tokenizeHtml(htmlString: String): ArrayDeque<HtmlToken> {
    val tokens = ArrayDeque<HtmlToken>()
    var i = 0
    @Suppress("LoopWithTooManyJumpStatements")
    outer@ while (i < htmlString.length) {
        var match = WHITESPACE_REGEX.matchAt(htmlString, i)
        if (match != null) {
            i = match.range.last + 1
            tokens.add(HtmlToken.Whitespace(match.value))
            continue
        }
        match = TAG_START_REGEX.matchAt(htmlString, i)
        if (match != null) {
            var k = match.range.last + 1
            val start = match.value
            val tagTokens = mutableListOf<TagToken>()
            while (k < htmlString.length) {
                var tagMatch = WHITESPACE_REGEX.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    continue
                }
                tagMatch = TAG_END_REGEX.matchAt(htmlString, k)
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
                tagMatch = TAG_EQUAL_REGEX.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagTokens.add(TagToken.Equal)
                    continue
                }
                tagMatch = TAG_QUOTE_REGEX.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagTokens.add(TagToken.Quote(tagMatch.groups[1]!!.value.escapeCharacters()))
                    continue
                }
                tagMatch = TAG_WORD_REGEX.matchAt(htmlString, k)
                if (tagMatch != null) {
                    k = tagMatch.range.last + 1
                    tagTokens.add(TagToken.Word(tagMatch.value))
                    continue
                }
            }
            // end of input found before end of tag so fall through
        }
        match = WORD_REGEX.matchAt(htmlString, i)
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
        val c = if (m.startsWith("#")) {
            m.substring(1).toIntOrNull()?.toChar()
        } else {
            ESCAPE_MAP[m]
        }
        if (c != null) {
            append(c)
        } else {
            append(match.value)
        }
        i = match.range.last + 1
        match = match.next()
    }
    if (i < this@escapeCharacters.length) {
        append(this@escapeCharacters.subSequence(i, this@escapeCharacters.length))
    }
}

private fun HtmlToken.Tag.isBreak(): Boolean = when (start) {
    "<br", "</br" -> true
    else -> false
}

private fun HtmlToken.Tag.isBlock(): Boolean = when (start) {
    "<p", "</p", "<pre", "</pre", "<h1", "</h1", "<h2", "</h2", "<h3", "</h3", "<h4", "</h4", "<h5", "</h5",
    "<h6", "</h6" -> true

    else -> false
}

private fun HtmlToken.Tag.isOpen(): Boolean = !start.startsWith("</")

private fun HtmlToken.Tag.isPreformatted(): Boolean = start == "<pre" || start == "/pre"
private fun HtmlToken.Tag.isHeader(): Boolean = start.startsWith("<h") || start.startsWith("</h")
private fun HtmlToken.Tag.toLevel(): Int = if (start.startsWith("</")) {
    start[3]
} else {
    start[2]
} - '1'

fun SpanStyle.toTextLinkStyles(): TextLinkStyles = TextLinkStyles(
    style = this,
    focusedStyle = copy(
        fontWeight = FontWeight.Bold,
    ),
    hoveredStyle = copy(
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline,
    ),
    pressedStyle = copy(
        fontWeight = FontWeight.ExtraBold,
        textDecoration = TextDecoration.Underline,
    ),
)

fun List<TagToken>.toAttributes(): List<String>? {
    var map: MutableList<String>? = null
    var i = 0
    @Suppress("LoopWithTooManyJumpStatements")
    while (i < this@toAttributes.size - 2) {
        if (this@toAttributes[i + 1] != TagToken.Equal) {
            i++
            continue
        }
        val key = when (val k = this@toAttributes[0]) {
            TagToken.Equal -> {
                i++
                continue
            }

            is TagToken.Quote -> {
                k.tag
            }

            is TagToken.Word -> {
                k.word
            }
        }
        val value = when (val k = this@toAttributes[2]) {
            TagToken.Equal -> {
                i++
                continue
            }

            is TagToken.Quote -> {
                k.tag
            }

            is TagToken.Word -> {
                k.word
            }
        }
        if (map == null) {
            map = mutableListOf()
        }
        map.add(key)
        map.add(value)
        i += 3
    }
    return map
}

@Suppress("CyclomaticComplexMethod")
private fun ParagraphStyle.applyAttributes(attributes: List<String>?): ParagraphStyle {
    if (attributes == null) return this
    return applyDir(attributes)
        .applyStyle(attributes)
}

@Suppress("CyclomaticComplexMethod")
private fun ParagraphStyle.applyDir(attributes: List<String>): ParagraphStyle {
    val dir = attributes.lookup("dir") ?: return this
    return when (dir.lowercase()) {
        "rtl" -> copy(textDirection = TextDirection.Rtl)
        "ltr" -> copy(textDirection = TextDirection.Ltr)
        else -> copy(textDirection = TextDirection.Unspecified)
    }
}

@Suppress("CyclomaticComplexMethod")
private fun ParagraphStyle.applyStyle(map: List<String>): ParagraphStyle {
    val style = map.lookup("style") ?: return this
    var s = this
    for (i in style.split(';')) {
        val keyValue = i.split(':')
        if (keyValue.size != 2) {
            continue
        }
        // font-size: 1.4rem;
        // color: red;
        // font-family: Helvetica, Arial, sans-serif;
        // font-size: 3rem;
        // text-transform: capitalize;
        // text-shadow: -1px -1px 1px #aaa,
        //              0px 2px 1px rgba(0,0,0,0.5),
        //              2px 2px 2px rgba(0,0,0,0.7),
        //              0px 0px 3px rgba(0,0,0,0.4);

        when (keyValue[0].trim().lowercase()) {
            "text-align" -> {
                s = when (keyValue[1].trim().lowercase()) {
                    "end" -> s.copy(textAlign = TextAlign.End)
                    "left" -> s.copy(textAlign = TextAlign.Left)
                    "right" -> s.copy(textAlign = TextAlign.Right)
                    "start" -> s.copy(textAlign = TextAlign.Start)
                    "center" -> s.copy(textAlign = TextAlign.Center)
                    "justify" -> s.copy(textAlign = TextAlign.Justify)
                    else -> s.copy(textAlign = TextAlign.Unspecified)
                }
            }

            "line-height" -> {
                val match = SIZE_REGEX.matchEntire(keyValue[1])
                s = if (match != null) {
                    val (scalar, units) = match.destructured
                    s.copy(
                        lineHeight = if (
                            units.equals("em", ignoreCase = true) ||
                            units.equals("%", ignoreCase = true) ||
                            units.equals("", ignoreCase = true)
                        ) {
                            scalar.toFloat().em
                        } else if (units.equals("px", ignoreCase = true)) {
                            scalar.toFloat().sp // or dp?
                        } else {
                            TextUnit.Unspecified
                        },
                    )
                } else {
                    s.copy(lineHeight = TextUnit.Unspecified)
                }
            }
        }
    }
    return s
}

val SIZE_REGEX = """^\s*(\d+(?:.\d+)?)([^\d\s]+)?\s*${'$'}""".toRegex(RegexOption.IGNORE_CASE)

private fun List<String>.lookup(key: String): String? {
    var i = 0
    while (i < size) {
        if (key.equals(this[i], ignoreCase = true)) {
            return this[i + 1]
        }
        i += 2
    }
    return null
}
