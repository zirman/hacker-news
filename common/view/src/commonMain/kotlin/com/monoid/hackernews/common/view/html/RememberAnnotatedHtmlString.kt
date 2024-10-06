package com.monoid.hackernews.common.view.html

import androidx.compose.material3.Typography
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// TODO:
// <span style=”...”>
// font-size: 1.4rem;
// color: red;
// background_color: yellow;
// font-family: Helvetica, Arial, sans-serif;
// font-size: 3rem;
// text-transform: capitalize;
// text-shadow: -1px -1px 1px #aaa,
//              0px 2px 1px rgba(0,0,0,0.5),
//              2px 2px 2px rgba(0,0,0,0.7),
//              0px 0px 3px black;
// text-shadow: 1px 1px 2px black;
// <ul
// <ol
// <dl
// <mark // highlight
// <q // inline quotation
// <wbr // possible line break
// <time
// <blockquote

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

fun annotateHtmlString(
    htmlString: String,
    typography: Typography,
    linkStyle: SpanStyle,
): AnnotatedString = HtmlParser(htmlString, typography, linkStyle).parse()

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

internal fun ParagraphStyle.applyAttributes(attributes: List<String>?): ParagraphStyle {
    if (attributes == null) return this
    return applyDir(attributes)
        .applyStyle(attributes)
}

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
