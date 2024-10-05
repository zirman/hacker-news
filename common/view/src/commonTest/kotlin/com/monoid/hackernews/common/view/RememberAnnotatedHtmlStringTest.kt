package com.monoid.hackernews.common.view

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.em
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("LargeClass")
class RememberAnnotatedHtmlStringTest {
    private val typography = Typography()
    private val linkStyle = SpanStyle(color = Color.Blue)

    @Test
    fun `tokenize 1`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Whitespace("  "),
                HtmlToken.Word("Hello"),
                HtmlToken.Whitespace("  \n  "),
                HtmlToken.Word("World!"),
                HtmlToken.Whitespace("  "),
            ),
            actual = tokenizeHtml("  Hello  \n  World!  ").toList(),
        )
    }

    @Test
    fun `tokenize 2`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Whitespace("  "),
                HtmlToken.Tag("<u", emptyList(), ">"),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
            ),
            actual = tokenizeHtml("  <u>Hello</u>").toList(),
        )
    }

    @Test
    fun `tokenize 3`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag("<u", emptyList(), ">"),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
                HtmlToken.Whitespace("  "),
            ),
            actual = tokenizeHtml("<u>Hello</u>  ").toList(),
        )
    }

    @Test
    fun `tokenize 4`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag("<u", emptyList(), ">"),
                HtmlToken.Whitespace("  "),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
            ),
            actual = tokenizeHtml("<u>  Hello</u>").toList(),
        )
    }

    @Test
    fun `tokenize 5`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag("<u", listOf(TagToken.Word("a")), ">"),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
            ),
            actual = tokenizeHtml("<u a>Hello</u>").toList(),
        )
    }

    @Test
    fun `tokenize 6`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag("<u", listOf(TagToken.Word("a"), TagToken.Equal), ">"),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
            ),
            actual = tokenizeHtml("<u a=>Hello</u>").toList(),
        )
    }

    @Test
    fun `tokenize 7`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag(
                    "<u",
                    listOf(TagToken.Word("a"), TagToken.Equal, TagToken.Word("b")),
                    ">"
                ),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
            ),
            actual = tokenizeHtml("<u a=b>Hello</u>").toList(),
        )
    }

    @Test
    fun `tokenize 8`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag(
                    "<u",
                    listOf(TagToken.Word("a"), TagToken.Equal, TagToken.Quote("b")),
                    ">"
                ),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
            ),
            actual = tokenizeHtml("""<u a="b">Hello</u>""").toList(),
        )
    }

    @Test
    fun `tokenize 9`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag(
                    "<u",
                    listOf(TagToken.Word("a"), TagToken.Equal, TagToken.Quote("=b")),
                    ">"
                ),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
            ),
            actual = tokenizeHtml("""<u a="=b">Hello</u>""").toList(),
        )
    }

    @Test
    fun `tokenize 10`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag("<br", emptyList(), "/>"),
            ),
            actual = tokenizeHtml("""<br />""").toList(),
        )
    }

    @Test
    fun `tokenize 11`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Whitespace("  "),
                HtmlToken.Tag("<u", emptyList(), ">"),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
            ),
            actual = tokenizeHtml("""  <u>Hello</u>""").toList(),
        )
    }

    @Test
    fun `tokenize 12`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag("<br", emptyList(), "/>"),
            ),
            actual = tokenizeHtml("""<br/>""").toList(),
        )
    }

    @Test
    fun `consolidate white space between words outside of tag`() {
        assertEquals(
            expected = buildAnnotatedString { append("Hello World!") },
            actual = annotateHtmlString("  Hello  \n  World!  ", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space before opening tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("  <u>Hello</u>", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space after closing tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("<u>Hello</u>  ", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space before words inside a tag 1`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("<u>  Hello</u>", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space before words inside a tag 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("Hello ")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("World!")
                pop()
            },
            actual = annotateHtmlString("Hello  <u>World!</u>", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space before words inside a tag 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("Hello")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append(" World!")
                pop()
            },
            actual = annotateHtmlString("Hello<u>  World!</u>", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space before words inside a tag 4`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("Hello ")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("World!")
                pop()
            },
            actual = annotateHtmlString("Hello  <u>  World!</u>", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space after words inside a tag 1`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
                pop()
                append(" World!")
            },
            actual = annotateHtmlString("<u>Hello</u>  World!", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space after words inside a tag 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello ")
                pop()
                append("World!")
            },
            actual = annotateHtmlString("<u>Hello  </u>World!", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space after words inside a tag 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello ")
                pop()
                append("World!")
            },
            actual = annotateHtmlString("<u>Hello  </u>  World!", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space between words inside a tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("<u>Hello  World!</u>  ", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("<u>  Hello  World!</u>", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space 4`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("  <u>  Hello World!</u>  ", typography, linkStyle),
        )
    }

    @Test
    fun `consolidate white space 5`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("<u>Hello  World!</u>  ", typography, linkStyle),
        )
    }

    @Test
    fun `annotated bold string 1`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<u>Hello  World!</u>""", typography, linkStyle),
        )
    }

    @Test
    fun `annotated bold string 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("H")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("ello Worl")
                pop()
                append("d!")
            },
            actual = annotateHtmlString("""H<u>ello  Worl</u>d!""", typography, linkStyle),
        )
    }

    @Test
    fun `annotated bold string 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("H ")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("ello Worl")
                pop()
                append("d!")
            },
            actual = annotateHtmlString("""H  <u>ello  Worl</u>d!""", typography, linkStyle),
        )
    }

    @Test
    fun `annotated bold string 4`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("H")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("ello Worl")
                pop()
                append(" d!")
            },
            actual = annotateHtmlString("""H<u>ello  Worl</u>  d!""", typography, linkStyle),
        )
    }

    @Test
    fun `annotated bold string 5`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("H")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("ello Worl")
                pop()
                append(" d!")
            },
            actual = annotateHtmlString("""H<u>ello  Worl</u>  d!  """, typography, linkStyle),
        )
    }

    @Test
    fun `annotated bold string 6`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("H")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("ello Worl ")
                pop()
                append("d!")
            },
            actual = annotateHtmlString("""H<u>ello  Worl  </u>  d!""", typography, linkStyle),
        )
    }

    @Test
    fun `annotated bold string 7`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append("Hello ")
                pop()
                append("World")
                pop()
            }, // consumes spaces between tags
            actual = annotateHtmlString(
                """  <u>  <s>  Hello  </s>World</u>  """,
                typography,
                linkStyle
            ),
        )
    }

    @Test
    fun `annotated bold string 8`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("Hello ")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("World ")
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append("Kotlin")
                pop()
                pop()
            }, // should consume spaces between tags
            actual = annotateHtmlString(
                htmlString = """  Hello  <u>  World  <s>  Kotlin  </s>  </u>  """,
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `annotated bold string 9`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append("Hello ")
                pop()
                append("World ")
                pop()
                append("Kotlin")
            }, // should consume spaces between tags
            actual = annotateHtmlString(
                htmlString = """  <u>  <s>  Hello  </s>  World  </u>  Kotlin  """,
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `b tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<b>Hello World!</b>""", typography, linkStyle),
        )
    }

    @Test
    fun `br tag 1`() {
        assertEquals(
            expected = buildAnnotatedString {
                appendLine()
            },
            actual = annotateHtmlString("""<br>""", typography, linkStyle),
        )
    }

    @Test
    fun `br tag 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                appendLine()
            },
            actual = annotateHtmlString("""<br/>""", typography, linkStyle),
        )
    }

    @Test
    fun `br tag 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                appendLine()
            },
            actual = annotateHtmlString("""</br>""", typography, linkStyle),
        )
    }

    @Test
    fun `br tag 4`() {
        assertEquals(
            expected = buildAnnotatedString {
                appendLine()
            },
            actual = annotateHtmlString("""<br />""", typography, linkStyle),
        )
    }

    @Test
    fun `br tag 5`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("Hello")
                appendLine()
                append("World!")
            },
            actual = annotateHtmlString("""Hello<br>World!""", typography, linkStyle),
        )
    }

    @Test
    fun `h1 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(typography.headlineLarge.toSpanStyle())
                append("Hello World!")
                pop()
                pop()
            },
            actual = annotateHtmlString("""<h1>Hello World!</h1>""", typography, linkStyle),
        )
    }

    @Test
    fun `h2 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(typography.headlineMedium.toSpanStyle())
                append("Hello World!")
                pop()
                pop()
            },
            actual = annotateHtmlString("""<h2>Hello World!</h2>""", typography, linkStyle),
        )
    }

    @Test
    fun `h3 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(typography.headlineSmall.toSpanStyle())
                append("Hello World!")
                pop()
                pop()
            },
            actual = annotateHtmlString("""<h3>Hello World!</h3>""", typography, linkStyle),
        )
    }

    @Test
    fun `h4 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(typography.titleLarge.toSpanStyle())
                append("Hello World!")
                pop()
                pop()
            },
            actual = annotateHtmlString("""<h4>Hello World!</h4>""", typography, linkStyle),
        )
    }

    @Test
    fun `h5 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(typography.titleMedium.toSpanStyle())
                append("Hello World!")
                pop()
                pop()
            },
            actual = annotateHtmlString("""<h5>Hello World!</h5>""", typography, linkStyle),
        )
    }

    @Test
    fun `h6 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(typography.titleSmall.toSpanStyle())
                append("Hello World!")
                pop()
                pop()
            },
            actual = annotateHtmlString("""<h6>Hello World!</h6>""", typography, linkStyle),
        )
    }

    @Test
    fun `header interleaving`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(typography.headlineLarge.toSpanStyle())
                append("Hello")
                pop()
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(typography.headlineMedium.toSpanStyle())
                append("World!")
                pop()
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(typography.headlineLarge.toSpanStyle())
                pop()
                pop()
            },
            actual = annotateHtmlString("""<h1>Hello<h2>World!</h1>""", typography, linkStyle),
        )
    }

    @Test
    fun `i tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<i>Hello World!</i>""", typography, linkStyle),
        )
    }

    @Test
    fun `cite tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<cite>Hello World!</cite>""", typography, linkStyle),
        )
    }

    @Test
    fun `dfn tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<dfn>Hello World!</dfn>""", typography, linkStyle),
        )
    }

    @Test
    fun `em tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<em>Hello World!</em>""", typography, linkStyle),
        )
    }

    @Test
    fun `big tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontSize = 1.25f.em))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<big>Hello World!</big>""", typography, linkStyle),
        )
    }

    @Test
    fun `small tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontSize = .8.em))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<small>Hello World!</small>""", typography, linkStyle),
        )
    }

    @Test
    fun `tt tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<tt>Hello World!</tt>""", typography, linkStyle),
        )
    }

    @Test
    fun `code tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString("""<code>Hello World!</code>""", typography, linkStyle),
        )
    }

    @Test
    fun `a tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushLink(
                    LinkAnnotation.Url(
                        url = "https://www.wikipedia.com/",
                        styles = linkStyle.toTextLinkStyles(),
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """<a href=https://www.wikipedia.com/>Hello World!</a>""",
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `pre tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                append("  Hello\n  World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """
                    |<pre>
                    |  Hello
                    |  World!  
                    |</pre>
                    |""".trimMargin(),
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `pre tag 1`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("  Hello\n  World!")
                pop()
                pop()
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """
                    |<u> <pre>
                    |  Hello
                    |  World!
                    |</pre> </u>
                    |""".trimMargin(),
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `pre tag 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("a")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("  Hello\n  World!")
                pop()
                pop()
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """
                    |a <u> <pre>
                    |  Hello
                    |  World!
                    |</pre> </u>
                    |""".trimMargin(),
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `pre tag 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                append("  Hello\n  World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """
                    |<pre>  Hello
                    |  World!
                    |</pre>
                    |""".trimMargin(),
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `pre tag 4`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                append("  ")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello\n  ")
                pop()
                append("World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """
                    |<pre>  <u>Hello
                    |  </u>World!
                    |</pre>
                    |""".trimMargin(),
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `s tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("""<s>Hello</s>""", typography, linkStyle),
        )
    }

    @Test
    fun `strike tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("""<strike>Hello</strike>""", typography, linkStyle),
        )
    }

    @Test
    fun `del tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("""<del>Hello</del>""", typography, linkStyle),
        )
    }

    @Test
    fun `u tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("""<u>Hello</u>""", typography, linkStyle),
        )
    }

    @Test
    fun `sup tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("""<sup>Hello</sup>""", typography, linkStyle),
        )
    }

    @Test
    fun `sub tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("""<sub>Hello</sub>""", typography, linkStyle),
        )
    }

    @Test
    fun `font tag monospace`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """<font face="monospace">Hello World!</font>""",
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `font tag serif`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Serif))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """<font face="serif">Hello World!</font>""",
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `font tag sans_serif`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.SansSerif))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """<font face="sans_serif">Hello World!</font>""",
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `font tag cursive`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Cursive))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """<font face="cursive">Hello World!</font>""",
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `font tag color`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(color = Color(0xff0000)))
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """<font color="#ff0000">Hello World!</font>""",
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `p tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                append("Hello")
                pop()
            },
            actual = annotateHtmlString("""<p>Hello</p>""", typography, linkStyle),
        )
    }

    @Test
    fun `p tag 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                append("Hello")
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                append("World!")
                pop()
            },
            actual = annotateHtmlString("""<p>Hello</p><p>World!</p>""", typography, linkStyle),
        )
    }

    @Test
    fun `p tag implied closure`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                append("Hello")
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                append("World!")
                pop()
            },
            actual = annotateHtmlString("""<p>Hello<p>World!""", typography, linkStyle),
        )
    }

    @Test
    fun `p tags enclosed in span tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
                pop()
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pop()
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("World!")
                pop()
                pop()
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pop()
            },
            actual = annotateHtmlString(
                """
                    |<u>
                    |  <p>Hello</p>
                    |  <p>World!</p>
                    |</u>
                    |""".trimMargin(),
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `p tag doesn't start until first word is output 1`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                append("Hello")
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pop()
            },
            actual = annotateHtmlString(
                """<p><p>Hello</p></p>""",
                typography = typography,
                linkStyle = linkStyle,
            ),
        )
    }

    @Test
    fun `p tag doesn't start until first word is output 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                append("Hello")
                pop()
                append("World!")
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pop()
                append("World!")
            },
            actual = annotateHtmlString("""<p>Hello</p>World!</p>World!""", typography, linkStyle),
        )
    }

    @Test
    fun `p tag doesn't start until first word is output 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pop()
                append("Hello")
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pop()
                append("World!")
            },
            actual = annotateHtmlString("""</p>Hello</p>World!""", typography, linkStyle),
        )
    }

    @Test
    fun `p tag doesn't start until first word is output 4`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                append("Hello World!")
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pop()
            },
            actual = annotateHtmlString(
                """<p><pre>Hello World!</pre></p>""",
                typography = typography,
                linkStyle = linkStyle
            ),
        )
    }

    @Test
    fun `p tag doesn't start until first word is output 5`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                append("Hello")
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pop()
                append("World!")
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                pop()
            },
            actual = annotateHtmlString("""<p><pre>Hello</p>World!</pre>""", typography, linkStyle),
        )
    }

    @Test
    fun `shuffling tags in paragraph`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
                pop()
                append(" World!")
                pop()
            },
            actual = annotateHtmlString("""<u><p>Hello</u> World!</p>""", typography, linkStyle),
        )
    }

    @Test
    fun `p with end alignment`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        textAlign = TextAlign.End,
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                """<p style="text-align: end;">Hello World!</p>""",
                typography,
                linkStyle
            ),
        )
    }

    @Test
    fun `p with start alignment`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        textAlign = TextAlign.Start,
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                """<p style="text-align: start;">Hello World!</p>""",
                typography,
                linkStyle
            ),
        )
    }

    @Test
    fun `p with center alignment`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        textAlign = TextAlign.Center,
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                """<p style="text-align: center;">Hello World!</p>""",
                typography,
                linkStyle
            ),
        )
    }

    @Test
    fun `p with justify alignment`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        textAlign = TextAlign.Justify,
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                """<p style="text-align: justify;">Hello World!</p>""",
                typography,
                linkStyle
            ),
        )
    }

    @Test
    fun `p with left alignment`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        textAlign = TextAlign.Left,
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                """<p style="text-align: left;">Hello World!</p>""",
                typography,
                linkStyle
            ),
        )
    }

    @Test
    fun `p with right alignment`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        textAlign = TextAlign.Right,
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                """<p style="text-align: right;">Hello World!</p>""",
                typography,
                linkStyle
            ),
        )
    }

    @Test
    fun `p with rtl alignment`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        textDirection = TextDirection.Rtl,
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                """<p dir=rtl>Hello World!</p>""",
                typography,
                linkStyle
            ),
        )
    }

    @Test
    fun `p with ltr alignment`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        textDirection = TextDirection.Ltr,
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                """<p dir=ltr>Hello World!</p>""",
                typography,
                linkStyle
            ),
        )
    }

    @Test
    fun `escape tab`() {
        assertEquals(
            expected = buildAnnotatedString { append("\t") },
            actual = annotateHtmlString("""&Tab;""", typography, linkStyle),
        )
    }

    @Test
    fun `escape newline`() {
        assertEquals(
            expected = buildAnnotatedString { append("\n") },
            actual = annotateHtmlString("""&NewLine;""", typography, linkStyle),
        )
    }

    @Test
    fun `escape ampersand`() {
        assertEquals(
            expected = buildAnnotatedString { append("&") },
            actual = annotateHtmlString("""&amp;""", typography, linkStyle),
        )
    }

    @Test
    fun `escape ampersand 2`() {
        assertEquals(
            expected = buildAnnotatedString { append("A&B") },
            actual = annotateHtmlString("""A&amp;B""", typography, linkStyle),
        )
    }

    @Test
    fun `escape less than`() {
        assertEquals(
            expected = buildAnnotatedString { append("<") },
            actual = annotateHtmlString("""&lt;""", typography, linkStyle),
        )
    }

    @Test
    fun `escape greater than`() {
        assertEquals(
            expected = buildAnnotatedString { append(">") },
            actual = annotateHtmlString("""&gt;""", typography, linkStyle),
        )
    }

    @Test
    fun `escape quote`() {
        assertEquals(
            expected = buildAnnotatedString { append("\"") },
            actual = annotateHtmlString("""&quot;""", typography, linkStyle),
        )
    }

    @Test
    fun `escape non breaking whitespace`() {
        assertEquals(
            expected = buildAnnotatedString { append('\u00a0') },
            actual = annotateHtmlString("""&nbsp;""", typography, linkStyle),
        )
    }

    @Test
    fun `escape apostrophe`() {
        assertEquals(
            expected = buildAnnotatedString { append("'") },
            actual = annotateHtmlString("""&#39;""", typography, linkStyle),
        )
    }

    @Test
    fun `escape two characters in a row`() {
        assertEquals(
            expected = buildAnnotatedString { append("A<B>C") },
            actual = annotateHtmlString("""A&lt;B&gt;C""", typography, linkStyle),
        )
    }
}
