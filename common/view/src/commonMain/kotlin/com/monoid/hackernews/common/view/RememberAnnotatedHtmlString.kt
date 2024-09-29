package com.monoid.hackernews.common.view

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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em

@Suppress("CyclomaticComplexMethod")
@Composable
fun rememberAnnotatedHtmlString(htmlString: String): AnnotatedString {
    val linkStyle = LocalLinkStyle.current.style ?: SpanStyle()
    return remember(htmlString, linkStyle) {
        annotateHtmlString(
            htmlString = htmlString,
            linkStyle = TextLinkStyles(
                style = linkStyle,
                focusedStyle = linkStyle,
                hoveredStyle = linkStyle,
                pressedStyle = linkStyle,
            ),
        )
    }
}

class HtmlParser(
    htmlString: String,
    private val textLinkStyles: TextLinkStyles,
) {
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
                    if (token.isBlock()) {
                        if (token.isOpen()) {
                            // push span styles up to index
                            pushBlock(index)
                            // pop all spans and block
                            repeat(stack.size) { pop() }
                            // drop block from stack
                            if (stack.firstOrNull()?.isBlock() == true) {
                                stack.removeFirst()
                            }
                            pushParagraphStyle(token)
                            // push spans
                            stack.forEach { pushStyleForSpanTag(it) }
                            // save block
                            stack.addFirst(token)
                        } else if (stack.firstOrNull()?.isBlock() == true) {
                            // handle close tag
                            repeat(stack.size) { pop() }
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
                    if (stack.firstOrNull()?.isPre() == true) {
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
        // ignore spaces at the end
        repeat(stack.size) { pop() }
        stack.clear()
    }

    private fun AnnotatedString.Builder.pushParagraphStyle(tag: HtmlToken.Tag) {
        pushStyle(
            ParagraphStyle(
                lineBreak = when (tag.start) {
                    "<p", "</p" -> LineBreak.Paragraph // TODO: apply alignment from attributes
                    "<pre", "</pre" -> LineBreak.Unspecified // TODO: disable soft wrap when possible
                    else -> throw IllegalStateException("Token doesn't have configured linebreak")
                },
            ),
        )
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
    // <h1-6

    private fun AnnotatedString.Builder.pushStyleForSpanTag(tag: HtmlToken.Tag) {
        when (tag.start) {
            "<b", "<strong" -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }

            "<i", "<cite", "<dfn", "<em", "<address" -> {
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
                // TODO: apply url from attributes
                pushLink(
                    LinkAnnotation.Url(
                        url = "https://www.google.com/",
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
}

// TODO
// CSS style: <span style=”color|background_color|text-decoration”>
fun annotateHtmlString(
    htmlString: String,
    linkStyle: TextLinkStyles,
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

@Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
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
        val c = if (m.startsWith("#")) {
            m.substring(1).toIntOrNull()?.toChar()
        } else {
            escapeMap[m]
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

private fun HtmlToken.Tag.isBlock(): Boolean = when (start) {
    "<p", "</p", "<pre", "</pre" -> true
    else -> false
}

private fun HtmlToken.Tag.isOpen(): Boolean = !start.startsWith("</")

private fun HtmlToken.Tag.isPre(): Boolean = start == "<pre"

private val escapeMap = mapOf(
    "amp" to '&',
    "lt" to '<',
    "gt" to '>',
    "quot" to '"',
    "nbsp" to '\u00a0', // Non-breaking space
    "Tab" to '\t',
    "NewLine" to '\n',
    "iexcl" to '¡',
    "cent" to '¢',
    "pound" to '£',
    "curren" to '¤',
    "yen" to '¥',
    "brvbar" to '¦',
    "sect" to '§',
    "uml" to '¨',
    "copy" to '©',
    "ordf" to 'ª',
    "laquo" to '«',
    "not" to '¬',
    "shy" to '\u00ad', // Soft hyphen
    "reg" to '®',
    "macr" to '¯',
    "deg" to '°',
    "plusmn" to '±',
    "sup2" to '²',
    "sup3" to '³',
    "acute" to '´',
    "micro" to 'µ',
    "para" to '¶',
    "dot" to '·',
    "cedil" to '¸',
    "sup1" to '¹',
    "ordm" to 'º',
    "raquo" to '»',
    "frac14" to '¼',
    "frac12" to '½',
    "frac34" to '¾',
    "iquest" to '¿',
    "Agrave" to 'À',
    "Aacute" to 'Á',
    "Acirc" to 'Â',
    "Atilde" to 'Ã',
    "Auml" to 'Ä',
    "Aring" to 'Å',
    "AElig" to 'Æ',
    "Ccedil" to 'Ç',
    "Egrave" to 'È',
    "Eacute" to 'É',
    "Ecirc" to 'Ê',
    "Euml" to 'Ë',
    "Igrave" to 'Ì',
    "Iacute" to 'Í',
    "Icirc" to 'Î',
    "Iuml" to 'Ï',
    "ETH" to 'Ð',
    "Ntilde" to 'Ñ',
    "Ograve" to 'Ò',
    "Oacute" to 'Ó',
    "Ocirc" to 'Ô',
    "Otilde" to 'Õ',
    "Ouml" to 'Ö',
    "times" to '×',
    "Oslash" to 'Ø',
    "Ugrave" to 'Ù',
    "Uacute" to 'Ú',
    "Ucirc" to 'Û',
    "Uuml" to 'Ü',
    "Yacute" to 'Ý',
    "THORN" to 'Þ',
    "szlig" to 'ß',
    "agrave" to 'à',
    "aacute" to 'á',
    "acirc" to 'â',
    "atilde" to 'ã',
    "auml" to 'ä',
    "aring" to 'å',
    "aelig" to 'æ',
    "ccedil" to 'ç',
    "egrave" to 'è',
    "eacute" to 'é',
    "ecirc" to 'ê',
    "euml" to 'ë',
    "igrave" to 'ì',
    "iacute" to 'í',
    "icirc" to 'î',
    "iuml" to 'ï',
    "eth" to 'ð',
    "ntilde" to 'ñ',
    "ograve" to 'ò',
    "oacute" to 'ó',
    "ocirc" to 'ô',
    "otilde" to 'õ',
    "ouml" to 'ö',
    "divide" to '÷',
    "oslash" to 'ø',
    "ugrave" to 'ù',
    "uacute" to 'ú',
    "ucirc" to 'û',
    "uuml" to 'ü',
    "yacute" to 'ý',
    "thorn" to 'þ',
    "yuml" to 'ÿ',
    "Amacr" to 'Ā',
    "amacr" to 'ā',
    "Abreve" to 'Ă',
    "abreve" to 'ă',
    "Aogon" to 'Ą',
    "aogon" to 'ą',
    "Cacute" to 'Ć',
    "cacute" to 'ć',
    "Ccirc" to 'Ĉ',
    "ccirc" to 'ĉ',
    "Cdot" to 'Ċ',
    "cdot" to 'ċ',
    "Ccaron" to 'Č',
    "ccaron" to 'č',
    "Dcaron" to 'Ď',
    "dcaron" to 'ď',
    "Dstrok" to 'Đ',
    "dstrok" to 'đ',
    "Emacr" to 'Ē',
    "emacr" to 'ē',
    "Ebreve" to 'Ĕ',
    "ebreve" to 'ĕ',
    "Edot" to 'Ė',
    "edot" to 'ė',
    "Eogon" to 'Ę',
    "eogon" to 'ę',
    "Ecaron" to 'Ě',
    "ecaron" to 'ě',
    "Gcirc" to 'Ĝ',
    "gcirc" to 'ĝ',
    "Gbreve" to 'Ğ',
    "gbreve" to 'ğ',
    "Gdot" to 'Ġ',
    "gdot" to 'ġ',
    "Gcedil" to 'Ģ',
    "gcedil" to 'ģ',
    "Hcirc" to 'Ĥ',
    "hcirc" to 'ĥ',
    "Hstrok" to 'Ħ',
    "hstrok" to 'ħ',
    "Itilde" to 'Ĩ',
    "itilde" to 'ĩ',
    "Imacr" to 'Ī',
    "imacr" to 'ī',
    "Ibreve" to 'Ĭ',
    "ibreve" to 'ĭ',
    "Iogon" to 'Į',
    "iogon" to 'į',
    "Idot" to 'İ',
    "imath" to 'ı',
    "IJlig" to 'Ĳ',
    "ijlig" to 'ĳ',
    "Jcirc" to 'Ĵ',
    "jcirc" to 'ĵ',
    "Kcedil" to 'Ķ',
    "kcedil" to 'ķ',
    "kgreen" to 'ĸ',
    "Lacute" to 'Ĺ',
    "lacute" to 'ĺ',
    "Lcedil" to 'Ļ',
    "lcedil" to 'ļ',
    "Lcaron" to 'Ľ',
    "lcaron" to 'ľ',
    "Lmidot" to 'Ŀ',
    "lmidot" to 'ŀ',
    "Lstrok" to 'Ł',
    "lstrok" to 'ł',
    "Nacute" to 'Ń',
    "nacute" to 'ń',
    "Ncedil" to 'Ņ',
    "ncedil" to 'ņ',
    "Ncaron" to 'Ň',
    "ncaron" to 'ň',
    "napos" to 'ŉ',
    "ENG" to 'Ŋ',
    "eng" to 'ŋ',
    "Omacr" to 'Ō',
    "omacr" to 'ō',
    "Obreve" to 'Ŏ',
    "obreve" to 'ŏ',
    "Odblac" to 'Ő',
    "odblac" to 'ő',
    "OElig" to 'Œ',
    "oelig" to 'œ',
    "Racute" to 'Ŕ',
    "racute" to 'ŕ',
    "Rcedil" to 'Ŗ',
    "rcedil" to 'ŗ',
    "Rcaron" to 'Ř',
    "rcaron" to 'ř',
    "Sacute" to 'Ś',
    "sacute" to 'ś',
    "Scirc" to 'Ŝ',
    "scirc" to 'ŝ',
    "Scedil" to 'Ş',
    "scedil" to 'ş',
    "Scaron" to 'Š',
    "scaron" to 'š',
    "Tcedil" to 'Ţ',
    "tcedil" to 'ţ',
    "Tcaron" to 'Ť',
    "tcaron" to 'ť',
    "Tstrok" to 'Ŧ',
    "tstrok" to 'ŧ',
    "Utilde" to 'Ũ',
    "utilde" to 'ũ',
    "Umacr" to 'Ū',
    "umacr" to 'ū',
    "Ubreve" to 'Ŭ',
    "ubreve" to 'ŭ',
    "Uring" to 'Ů',
    "uring" to 'ů',
    "Udblac" to 'Ű',
    "udblac" to 'ű',
    "Uogon" to 'Ų',
    "uogon" to 'ų',
    "Wcirc" to 'Ŵ',
    "wcirc" to 'ŵ',
    "Ycirc" to 'Ŷ',
    "ycirc" to 'ŷ',
    "Yuml" to 'Ÿ',
    "fnof" to 'ƒ',
    "circ" to 'ˆ',
    "tilde" to '˜',
    "Alpha" to 'Α',
    "Beta" to 'Β',
    "Gamma" to 'Γ',
    "Delta" to 'Δ',
    "Epsilon" to 'Ε',
    "Zeta" to 'Ζ',
    "Eta" to 'Η',
    "Theta" to 'Θ',
    "Iota" to 'Ι',
    "Kappa" to 'Κ',
    "Lambda" to 'Λ',
    "Mu" to 'Μ',
    "Nu" to 'Ν',
    "Xi" to 'Ξ',
    "Omicron" to 'Ο',
    "Pi" to 'Π',
    "Rho" to 'Ρ',
    "Sigma" to 'Σ',
    "Tau" to 'Τ',
    "Upsilon" to 'Υ',
    "Phi" to 'Φ',
    "Chi" to 'Χ',
    "Psi" to 'Ψ',
    "Omega" to 'Ω',
    "alpha" to 'α',
    "beta" to 'β',
    "gamma" to 'γ',
    "delta" to 'δ',
    "epsilon" to 'ε',
    "zeta" to 'ζ',
    "eta" to 'η',
    "theta" to 'θ',
    "iota" to 'ι',
    "kappa" to 'κ',
    "lambda" to 'λ',
    "mu" to 'μ',
    "nu" to 'ν',
    "xi" to 'ξ',
    "omicron" to 'ο',
    "pi" to 'π',
    "rho" to 'ρ',
    "sigmaf" to 'ς',
    "sigma" to 'σ',
    "tau" to 'τ',
    "upsilon" to 'υ',
    "phi" to 'φ',
    "chi" to 'χ',
    "psi" to 'ψ',
    "omega" to 'ω',
    "thetasym" to 'ϑ',
    "upsih" to 'ϒ',
    "piv" to 'ϖ',
    "ensp" to '\u2002', // En space
    "emsp" to '\u2003', // Em space
    "thinsp" to '\u2009', // Thin space
    "zwnj" to '\u200C', // Zero width non-joiner
    "zwj" to '\u200D', // Zero width joiner
    "lrm" to '\u200E', // Left-to-right mark
    "rlm" to '\u200F', // Right-to-left mark
    "ndash" to '–',
    "mdash" to '—',
    "lsquo" to '‘',
    "rsquo" to '’',
    "sbquo" to '‚',
    "ldquo" to '“',
    "rdquo" to '”',
    "bdquo" to '„',
    "dagger" to '†',
    "Dagger" to '‡',
    "bull" to '•',
    "hellip" to '…',
    "permil" to '‰',
    "prime" to '′',
    "Prime" to '″',
    "lsaquo" to '‹',
    "rsaquo" to '›',
    "oline" to '‾',
    "euro" to '€',
    "trade" to '™',
    "larr" to '←',
    "uarr" to '↑',
    "rarr" to '→',
    "darr" to '↓',
    "harr" to '↔',
    "crarr" to '↵',
    "forall" to '∀',
    "part" to '∂',
    "exist" to '∃',
    "empty" to '∅',
    "nabla" to '∇',
    "isin" to '∈',
    "notin" to '∉',
    "ni" to '∋',
    "prod" to '∏',
    "sum" to '∑',
    "minus" to '−',
    "lowast" to '∗',
    "radic" to '√',
    "prop" to '∝',
    "infin" to '∞',
    "ang" to '∠',
    "and" to '∧',
    "or" to '∨',
    "cap" to '∩',
    "cup" to '∪',
    "int" to '∫',
    "there4" to '∴',
    "sim" to '∼',
    "cong" to '≅',
    "asymp" to '≈',
    "ne" to '≠',
    "equiv" to '≡',
    "le" to '≤',
    "ge" to '≥',
    "sub" to '⊂',
    "sup" to '⊃',
    "nsub" to '⊄',
    "sube" to '⊆',
    "supe" to '⊇',
    "oplus" to '⊕',
    "otimes" to '⊗',
    "perp" to '⊥',
    "sdot" to '⋅',
    "lceil" to '⌈',
    "rceil" to '⌉',
    "lfloor" to '⌊',
    "rfloor" to '⌋',
    "loz" to '◊',
    "spades" to '♠',
    "clubs" to '♣',
    "hearts" to '♥',
    "diams" to '♦',
)
