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
import androidx.compose.ui.text.style.TextDecoration
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
                        url = "https://www.google.com/",
                        styles = linkStyle.toTextLinkStyles(),
                    ),
                )
                append("Hello World!")
                pop()
            },
            actual = annotateHtmlString(
                htmlString = """<a href="https://www.google.com/">Hello World!</a>""",
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
                """
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
                """
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
                """
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
                """
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
