package com.monoid.hackernews.common.data.html

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
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("LargeClass")
class RememberAnnotatedHtmlStringTest {
    private val htmlParser = HtmlParser()

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
            actual = htmlParser.parse("  Hello  \n  World!  "),
        )
    }

    @Test
    fun `consolidate white space before opening tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
            },
            actual = htmlParser.parse("  <u>Hello</u>"),
        )
    }

    @Test
    fun `consolidate white space after closing tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
            },
            actual = htmlParser.parse("<u>Hello</u>  "),
        )
    }

    @Test
    fun `consolidate white space before words inside a tag 1`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
            },
            actual = htmlParser.parse("<u>  Hello</u>"),
        )
    }

    @Test
    fun `consolidate white space before words inside a tag 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("Hello ")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("World!")
            },
            actual = htmlParser.parse("Hello  <u>World!</u>"),
        )
    }

    @Test
    fun `consolidate white space before words inside a tag 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("Hello")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append(" World!")
            },
            actual = htmlParser.parse("Hello<u>  World!</u>"),
        )
    }

    @Test
    fun `consolidate white space before words inside a tag 4`() {
        assertEquals(
            expected = buildAnnotatedString {
                append("Hello ")
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("World!")
            },
            actual = htmlParser.parse("Hello  <u>  World!</u>"),
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
            actual = htmlParser.parse("<u>Hello</u>  World!"),
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
            actual = htmlParser.parse("<u>Hello  </u>World!"),
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
            actual = htmlParser.parse("<u>Hello  </u>  World!"),
        )
    }

    @Test
    fun `consolidate white space between words inside a tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
            },
            actual = htmlParser.parse("<u>Hello  World!</u>  "),
        )
    }

    @Test
    fun `consolidate white space 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
            },
            actual = htmlParser.parse("<u>  Hello  World!</u>"),
        )
    }

    @Test
    fun `consolidate white space 4`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
            },
            actual = htmlParser.parse("  <u>  Hello World!</u>  "),
        )
    }

    @Test
    fun `consolidate white space 5`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
            },
            actual = htmlParser.parse("<u>Hello  World!</u>  "),
        )
    }

    @Test
    fun `annotated bold string 1`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<u>Hello  World!</u>"""),
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
            actual = htmlParser.parse("""H<u>ello  Worl</u>d!"""),
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
            actual = htmlParser.parse("""H  <u>ello  Worl</u>d!"""),
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
            actual = htmlParser.parse("""H<u>ello  Worl</u>  d!"""),
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
            actual = htmlParser.parse("""H<u>ello  Worl</u>  d!  """),
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
            actual = htmlParser.parse("""H<u>ello  Worl  </u>  d!"""),
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
            }, // consumes spaces between tags
            actual = htmlParser.parse("""  <u>  <s>  Hello  </s>World</u>  """),
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
            }, // should consume spaces between tags
            actual = htmlParser.parse("""  Hello  <u>  World  <s>  Kotlin  </s>  </u>  """),
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
            actual = htmlParser.parse("""  <u>  <s>  Hello  </s>  World  </u>  Kotlin  """),
        )
    }

    @Test
    fun `b tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<b>Hello World!</b>"""),
        )
    }

    @Test
    fun `br tag 1`() {
        assertEquals(
            expected = buildAnnotatedString {
                appendLine()
            },
            actual = htmlParser.parse("""<br>"""),
        )
    }

    @Test
    fun `br tag 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                appendLine()
            },
            actual = htmlParser.parse("""<br/>"""),
        )
    }

    @Test
    fun `br tag 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                appendLine()
            },
            actual = htmlParser.parse("""</br>"""),
        )
    }

    @Test
    fun `br tag 4`() {
        assertEquals(
            expected = buildAnnotatedString {
                appendLine()
            },
            actual = htmlParser.parse("""<br />"""),
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
            actual = htmlParser.parse("""Hello<br>World!"""),
        )
    }

    @Test
    fun `h1 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(htmlParser.hStyles[0])
                append("Hello World!")
            },
            actual = htmlParser.parse("""<h1>Hello World!</h1>"""),
        )
    }

    @Test
    fun `h2 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(htmlParser.hStyles[1])
                append("Hello World!")
            },
            actual = htmlParser.parse("""<h2>Hello World!</h2>"""),
        )
    }

    @Test
    fun `h3 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(htmlParser.hStyles[2])
                append("Hello World!")
            },
            actual = htmlParser.parse("""<h3>Hello World!</h3>"""),
        )
    }

    @Test
    fun `h4 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(htmlParser.hStyles[3])
                append("Hello World!")
            },
            actual = htmlParser.parse("""<h4>Hello World!</h4>"""),
        )
    }

    @Test
    fun `h5 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(htmlParser.hStyles[4])
                append("Hello World!")
            },
            actual = htmlParser.parse("""<h5>Hello World!</h5>"""),
        )
    }

    @Test
    fun `h6 tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(htmlParser.hStyles[5])
                append("Hello World!")
            },
            actual = htmlParser.parse("""<h6>Hello World!</h6>"""),
        )
    }

    @Test
    fun `header interleaving`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(htmlParser.hStyles[0])
                append("Hello")
                pop()
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(htmlParser.hStyles[1])
                append("World!")
                pop()
                pop()
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Heading))
                pushStyle(htmlParser.hStyles[0])
            },
            actual = htmlParser.parse("""<h1>Hello<h2>World!</h1>"""),
        )
    }

    @Test
    fun `i tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<i>Hello World!</i>"""),
        )
    }

    @Test
    fun `cite tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<cite>Hello World!</cite>"""),
        )
    }

    @Test
    fun `dfn tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<dfn>Hello World!</dfn>"""),
        )
    }

    @Test
    fun `em tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<em>Hello World!</em>"""),
        )
    }

    @Test
    fun `big tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontSize = 1.25f.em))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<big>Hello World!</big>"""),
        )
    }

    @Test
    fun `small tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontSize = .8.em))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<small>Hello World!</small>"""),
        )
    }

    @Test
    fun `tt tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<tt>Hello World!</tt>"""),
        )
    }

    @Test
    fun `code tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<code>Hello World!</code>"""),
        )
    }

    @Test
    fun `a tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushLink(
                    LinkAnnotation.Url(
                        url = "https://www.wikipedia.com/",
                        styles = htmlParser.textLinkStyles,
                    ),
                )
                append("Hello World!")
            },
            actual = htmlParser.parse("""<a href=https://www.wikipedia.com/>Hello World!</a>"""),
        )
    }

    @Test
    fun `pre tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                append("  Hello\n  World!")
            },
            actual = htmlParser.parse(
                htmlString = """
                    |<pre>
                    |  Hello
                    |  World!  
                    |</pre>
                    |""".trimMargin(),
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
            },
            actual = htmlParser.parse(
                htmlString = """
                    |<u> <pre>
                    |  Hello
                    |  World!
                    |</pre> </u>
                    |""".trimMargin(),
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
            },
            actual = htmlParser.parse(
                htmlString = """
                    |a <u> <pre>
                    |  Hello
                    |  World!
                    |</pre> </u>
                    |""".trimMargin(),
            ),
        )
    }

    @Test
    fun `pre tag 3`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Unspecified))
                append("  Hello\n  World!")
            },
            actual = htmlParser.parse(
                htmlString = """
                    |<pre>  Hello
                    |  World!
                    |</pre>
                    |""".trimMargin(),
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
            },
            actual = htmlParser.parse(
                htmlString = """
                    |<pre>  <u>Hello
                    |  </u>World!
                    |</pre>
                    |""".trimMargin(),
            ),
        )
    }

    @Test
    fun `s tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append("Hello")
            },
            actual = htmlParser.parse("""<s>Hello</s>"""),
        )
    }

    @Test
    fun `strike tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append("Hello")
            },
            actual = htmlParser.parse("""<strike>Hello</strike>"""),
        )
    }

    @Test
    fun `del tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                append("Hello")
            },
            actual = htmlParser.parse("""<del>Hello</del>"""),
        )
    }

    @Test
    fun `u tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                append("Hello")
            },
            actual = htmlParser.parse("""<u>Hello</u>"""),
        )
    }

    @Test
    fun `sup tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
                append("Hello")
            },
            actual = htmlParser.parse("""<sup>Hello</sup>"""),
        )
    }

    @Test
    fun `sub tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
                append("Hello")
            },
            actual = htmlParser.parse("""<sub>Hello</sub>"""),
        )
    }

    @Test
    fun `font tag monospace`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<font face="monospace">Hello World!</font>"""),
        )
    }

    @Test
    fun `font tag serif`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Serif))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<font face="serif">Hello World!</font>"""),
        )
    }

    @Test
    fun `font tag sans_serif`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.SansSerif))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<font face="sans_serif">Hello World!</font>"""),
        )
    }

    @Test
    fun `font tag cursive`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(fontFamily = FontFamily.Cursive))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<font face="cursive">Hello World!</font>"""),
        )
    }

    @Test
    fun `font tag color`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(SpanStyle(color = Color(0xff0000)))
                append("Hello World!")
            },
            actual = htmlParser.parse("""<font color="#ff0000">Hello World!</font>"""),
        )
    }

    @Test
    fun `p tag`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(ParagraphStyle(lineBreak = LineBreak.Paragraph))
                append("Hello")
            },
            actual = htmlParser.parse("""<p>Hello</p>"""),
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
            },
            actual = htmlParser.parse("""<p>Hello</p><p>World!</p>"""),
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
            },
            actual = htmlParser.parse("""<p>Hello<p>World!"""),
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
            },
            actual = htmlParser.parse(
                htmlString = """
                     |<u>
                     |  <p>Hello</p>
                     |  <p>World!</p>
                     |</u>
                     |""".trimMargin(),
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
            },
            actual = htmlParser.parse("""<p><p>Hello</p></p>"""),
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
            actual = htmlParser.parse("""<p>Hello</p>World!</p>World!"""),
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
            actual = htmlParser.parse("""</p>Hello</p>World!"""),
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
            },
            actual = htmlParser.parse("""<p><pre>Hello World!</pre></p>"""),
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
            },
            actual = htmlParser.parse("""<p><pre>Hello</p>World!</pre>"""),
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
            },
            actual = htmlParser.parse("""<u><p>Hello</u> World!</p>"""),
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
            },
            actual = htmlParser.parse("""<p style="text-align: end;">Hello World!</p>"""),
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
            },
            actual = htmlParser.parse("""<p style="text-align: start;">Hello World!</p>"""),
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
            },
            actual = htmlParser.parse("""<p style="text-align: center;">Hello World!</p>"""),
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
            },
            actual = htmlParser.parse("""<p style="text-align: justify;">Hello World!</p>"""),
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
            },
            actual = htmlParser.parse("""<p style="text-align: left;">Hello World!</p>"""),
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
            },
            actual = htmlParser.parse("""<p style="text-align: right;">Hello World!</p>"""),
        )
    }

    @Test
    fun `p with line-height em`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        lineHeight = 2.em,
                    ),
                )
                append("Hello World!")
            },
            actual = htmlParser.parse("""<p style="line-height: 2em;">Hello World!</p>"""),
        )
    }

    @Test
    fun `p with line-height em 2`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        lineHeight = 2.5.em,
                    ),
                )
                append("Hello World!")
            },
            actual = htmlParser.parse("""<p style="line-height: 2.5em;">Hello World!</p>"""),
        )
    }

    @Test
    fun `p with line-height percent`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        lineHeight = 2.5.em,
                    ),
                )
                append("Hello World!")
            },
            actual = htmlParser.parse("""<p style="line-height: 2.5%;">Hello World!</p>"""),
        )
    }

    @Test
    fun `p with line-height`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        lineHeight = 2.5.em,
                    ),
                )
                append("Hello World!")
            },
            actual = htmlParser.parse("""<p style="line-height: 2.5;">Hello World!</p>"""),
        )
    }

    @Test
    fun `p with line-height px`() {
        assertEquals(
            expected = buildAnnotatedString {
                pushStyle(
                    ParagraphStyle(
                        lineBreak = LineBreak.Paragraph,
                        lineHeight = 16.sp,
                    ),
                )
                append("Hello World!")
            },
            actual = htmlParser.parse("""<p style="line-height: 16px;">Hello World!</p>"""),
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
            },
            actual = htmlParser.parse("""<p dir=rtl>Hello World!</p>"""),
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
            },
            actual = htmlParser.parse("""<p dir=ltr>Hello World!</p>"""),
        )
    }

    @Test
    fun `escape tab`() {
        assertEquals(
            expected = buildAnnotatedString { append("\t") },
            actual = htmlParser.parse("""&Tab;"""),
        )
    }

    @Test
    fun `escape newline`() {
        assertEquals(
            expected = buildAnnotatedString { append("\n") },
            actual = htmlParser.parse("""&NewLine;"""),
        )
    }

    @Test
    fun `escape ampersand`() {
        assertEquals(
            expected = buildAnnotatedString { append("&") },
            actual = htmlParser.parse("""&amp;"""),
        )
    }

    @Test
    fun `escape ampersand 2`() {
        assertEquals(
            expected = buildAnnotatedString { append("A&B") },
            actual = htmlParser.parse("""A&amp;B"""),
        )
    }

    @Test
    fun `escape less than`() {
        assertEquals(
            expected = buildAnnotatedString { append("<") },
            actual = htmlParser.parse("""&lt;"""),
        )
    }

    @Test
    fun `escape greater than`() {
        assertEquals(
            expected = buildAnnotatedString { append(">") },
            actual = htmlParser.parse("""&gt;"""),
        )
    }

    @Test
    fun `escape quote`() {
        assertEquals(
            expected = buildAnnotatedString { append("\"") },
            actual = htmlParser.parse("""&quot;"""),
        )
    }

    @Test
    fun `escape non breaking whitespace`() {
        assertEquals(
            expected = buildAnnotatedString { append('\u00a0') },
            actual = htmlParser.parse("""&nbsp;"""),
        )
    }

    @Test
    fun `escape apostrophe`() {
        assertEquals(
            expected = buildAnnotatedString { append("'") },
            actual = htmlParser.parse("""&#39;"""),
        )
    }

    @Test
    fun `escape apostrophe in hex`() {
        assertEquals(
            expected = buildAnnotatedString { append("'") },
            actual = htmlParser.parse("""&#x27;"""),
        )
    }

    @Test
    fun `escape two characters in a row`() {
        assertEquals(
            expected = buildAnnotatedString { append("A<B>C") },
            actual = htmlParser.parse("""A&lt;B&gt;C"""),
        )
    }
}