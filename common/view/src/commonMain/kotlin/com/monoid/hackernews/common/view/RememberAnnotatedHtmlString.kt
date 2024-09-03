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
private val fooRegex = """\s*>""".toRegex(RegexOption.IGNORE_CASE)
private val endTagRegex =
    """</([a-z][a-z0-9_]*)\s*>"""
        .toRegex(RegexOption.IGNORE_CASE)
//private val wordRegex =
//    """\s*(\w+)""".toRegex()

@Suppress("CyclomaticComplexMethod")
@Composable
fun rememberAnnotatedHtmlString(htmlString: String): AnnotatedString {
    val linkStyle = LocalLinkStyle.current.style
    val fontSize = LocalTextStyle.current.fontSize
    return remember(htmlString, linkStyle, fontSize) {
        // TODO
        // Setting font properties: <font face=”font_family“ color=”hex_color”>. Examples of possible font
        // families include monospace, serif, and sans_serif.
        // CSS style: <span style=”color|background_color|text-decoration”>
        // Paragraphs: <p dir="rtl | ltr" style="">
        buildAnnotatedString {
            var ulDepth = 0

            @Suppress("ReturnCount")
            fun recur(index: Int): Int {
                val startTagMatch = startTagRegex.matchAt(htmlString, index)
//                println("matchAt ${htmlString.substring(index)} ${startTagMatch}")
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
//                        ?.also { println("attributes:$it") }
                    i = fooRegex.matchAt(htmlString, i)!!.range.last + 1
                    val tag = startTagMatch.groupValues[1].lowercase()
//                    println("tag:$tag")
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
                            appendLine()
                            repeat(ulDepth) {
                                append("  ")
                            }
                            append("• ")
                        }

                        "div" -> {
                            appendLine()
                            appendLine()
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
                            "del", "u", "sup", "sub" -> {
                                pop()
                            }

                            "br" -> {}

                            "ul" -> {
                                ulDepth--
                            }

                            "li" -> {
                                appendLine()
                                appendLine()
                            }

                            "div" -> {
                                appendLine()
                            }

                            "a" -> {
                                pop()
                                if (linkStyle != null) {
                                    pop()
                                }
                            }
                        }
                        return endTagMatch.range.last + 1
                    }
                } else { // no tag so we consume characters up to the next tag
                    val textMatch = textRegex.matchAt(htmlString, index)
                    if (textMatch != null) {
//                        val x = wordRegex.matchAt(nextTagMatch.value, 0)
//                        if (x != null) {
//                            append(x.groupValues[1])
//                            var y =
//                            while (y != null) {
//                                append(' ')
//                                append(y.value)
//                                y =
//                            }
//                            // remove extra white space
//                            nextTagMatch.value
//                        }
//                        println("text ${textMatch.value}")
                        append(textMatch.value)
                        return textMatch.range.last + 1
                    } else {
                        return index
                    }
                }
            }
            // loop through consuming top level tags and text
            var i = 0
            while (i < htmlString.length) {
//                val x = recur(i)
//                println("asdf $i $x")
//                if (x == i) break
//                i = x
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
