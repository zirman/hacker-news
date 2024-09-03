package com.monoid.hackernews.common.view

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration

private val textRegex = """[^<]+""".toRegex(RegexOption.IGNORE_CASE)
private val startTagRegex = """<([a-z][a-z0-9_]*)""".toRegex(RegexOption.IGNORE_CASE)
private val attributeRegex =
    """\s*(?<![a-z0-9_])([a-z][a-z0-9_]*)(\s*=\s*("([^"]*)"|([a-z][a-z0-9_]*)))?"""
        .toRegex(RegexOption.IGNORE_CASE)
private val capRegex = """\s*>""".toRegex(RegexOption.IGNORE_CASE)
private val endTagRegex = """</([a-z][a-z0-9_]*)\s*>""".toRegex(RegexOption.IGNORE_CASE)
private val wordRegex = """\w+""".toRegex(RegexOption.IGNORE_CASE)

@Suppress("CyclomaticComplexMethod")
@Composable
fun rememberAnnotatedHtmlString(htmlString: String): AnnotatedString {
    val linkStyle = LocalLinkStyle.current.style
    val fontSize = LocalTextStyle.current.fontSize
    return remember(htmlString, linkStyle, fontSize) {
        // TODO
        // CSS style: <span style=”color|background_color|text-decoration”>
        // Paragraphs: <p dir="rtl | ltr" style="">
        buildAnnotatedString {
            var ulDepth = 0
            var prependSpace = false
            var onNewLine = true

            @Suppress("ReturnCount")
            fun recur(index: Int): Int {
                if (prependSpace) {
                    append(' ')
                    prependSpace = false
                }
                val startTagMatch = startTagRegex.matchAt(htmlString, index)
                // found a tag now recurse and then find matching close tag
                if (startTagMatch != null) {
                    var i = startTagMatch.range.last + 1
                    val attributes = attributeRegex
                        .matchAt(htmlString, i)
                        ?.let { firstMatch ->
                            buildMap {
                                var match: MatchResult? = firstMatch
                                while (match != null) {
                                    i = match.range.last + 1
                                    val groupValues = match.groupValues
                                    put(
                                        groupValues[1].lowercase(),
                                        groupValues[4].ifEmpty { groupValues[5] },
                                    )
                                    match = attributeRegex.matchAt(htmlString, i)
                                }
                            }
                        }
                    i = capRegex.matchAt(htmlString, i)!!.range.last + 1
                    val tag = startTagMatch.groupValues[1].lowercase()
                    when (tag) {
                        "b" -> {
                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        }

                        "i", "cite", "dfn", "em" -> {
                            pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        }

                        "big" -> {
                            pushStyle(SpanStyle(fontSize = fontSize * 1.25))
                        }

                        "small" -> {
                            pushStyle(SpanStyle(fontSize = fontSize * 0.8))
                        }

                        "tt" -> {
                            pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                        }

                        "s", "strike", "del" -> {
                            pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                        }

                        "u" -> {
                            pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                        }

                        "sup" -> {
                            pushStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
                        }

                        "sub" -> {
                            pushStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
                        }

                        "br" -> {
                            appendLine()
                        }

                        "ul" -> {
                            ulDepth++
                        }

                        "li" -> {
                            // TODO: only do if previous didn't end with newline
                            if (!onNewLine) {
                                appendLine()
                            }
                            repeat(ulDepth) {
                                append("  ")
                            }
                            append("• ")
                        }

                        "div" -> {
                            // TODO: only do if previous didn't end with newline
                            if (!onNewLine) {
                                appendLine()
                            }
                        }

                        "a" -> {
                            val href = attributes?.get("href")
                            if (href != null) {
                                pushLink(LinkAnnotation.Url(href))
                                if (linkStyle != null) {
                                    pushStyle(linkStyle)
                                }
                            }
                        }

                        "p" -> {
                            // TODO: only do if previous didn't end with newline
                            if (!onNewLine) {
                                appendLine()
                            }
                        }

                        "font" -> {
                            // Setting font properties: <font face=”font_family“ color=”hex_color”>.
                            // Examples of possible font families include monospace, serif, and sans_serif.
                            val fontFamily = attributes?.get("face")
                            val color = attributes?.get("color")
                            when (fontFamily?.lowercase()) {
                                "monospace" -> {
                                    pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                                }

                                "serif" -> {
                                    pushStyle(SpanStyle(fontFamily = FontFamily.Serif))
                                }

                                "sans_serif" -> {
                                    pushStyle(SpanStyle(fontFamily = FontFamily.SansSerif))
                                }

                                else -> {
                                    pushStyle(SpanStyle())
                                }
                            }
                        }
                    }
                    i = recur(i)
                    val endTagMatch = endTagRegex.matchAt(htmlString, i)
                    if (endTagMatch == null) {
                        if (tag != "br") {
                            println("tag mismatch error")
                        }
                        return i
                    } else if (endTagMatch.groupValues[1] != tag) {
                        if (tag != "br") {
                            println("tag mismatch error")
                        }
                        return i
                    } else {
                        when (tag) {
                            "b", "i", "cite", "dfn", "em", "big", "small", "tt", "s", "strike",
                            "del", "u", "sup", "sub", "font" -> {
                                pop()
                            }

                            "br" -> {
                            }

                            "ul" -> {
                                ulDepth--
                                appendLine()
                                onNewLine = true
                            }

                            "li" -> {
                                appendLine()
                                onNewLine = true
                            }

                            "div" -> {
                                appendLine()
                                onNewLine = true
                            }

                            "p" -> {
                                appendLine()
                                onNewLine = true
                            }

                            "a" -> {
                                if (attributes?.get("href") != null) {
                                    pop()
                                    if (linkStyle != null) {
                                        pop()
                                    }
                                }
                            }
                        }
                        return endTagMatch.range.last + 1
                    }
                } else { // no tag so we consume characters up to the next tag
                    val textMatch = textRegex.matchAt(htmlString, index)
                    if (textMatch != null) {
                        var wordMatch = wordRegex.find(textMatch.value)
                        if (onNewLine.not() && prependSpace.not() && (wordMatch?.range?.first ?: 1) > 0) {
                            append(' ')
                        }
                        onNewLine = false
                        while (wordMatch != null) {
                            append(wordMatch.value)
                            prependSpace = textMatch.value.length != wordMatch.range.last + 1
                            wordMatch = wordMatch.next()
                            if (wordMatch != null) {
                                append(' ')
                            }
                        }
                        return textMatch.range.last + 1
                    } else {
                        return index
                    }
                }
            }
            // loop through consuming top level tags and text
            var i = 0
            while (i < htmlString.length) {
                i = recur(i)
            }
        }
    }
}

// Hacker News Formatting
// Blank lines separate paragraphs.
// Text surrounded by asterisks is italicized. To get a literal asterisk, use \* or **.
// Text after a blank line that is indented by two or more spaces is reproduced verbatim. (This is intended for code.)
// Urls become links, except in the text field of a submission.
// If your url gets linked incorrectly, put it in <angle brackets> and it should work.
