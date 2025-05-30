package com.monoid.hackernews.common.data.html

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.monoid.hackernews.common.data.WeakHashMap

// TODO:
// <span style="...">
// font-size: 1.4rem;
// color: red;
// background-color: yellow;
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

private val htmlParserHash: WeakHashMap<String, AnnotatedString> = WeakHashMap()
private val htmlParser = HtmlParser()
fun String.toHtmlAnnotatedString(): AnnotatedString = htmlParserHash.getOrPut(this) {
    htmlParser.parse(this)
}

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
                    val scalar = checkNotNull(match.groups[1]).value
                    val units = match.groups[2]?.value
                    s.copy(
                        lineHeight = if (
                            units == null ||
                            units.equals("em", ignoreCase = true) ||
                            units.equals("%", ignoreCase = true)
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

private val SIZE_REGEX =
    """^\s*(\d+(?:.\d+)?)([^\d\s]+)?\s*${'$'}""".toRegex(RegexOption.IGNORE_CASE)
