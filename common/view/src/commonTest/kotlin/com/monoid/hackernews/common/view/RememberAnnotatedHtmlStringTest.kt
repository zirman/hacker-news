package com.monoid.hackernews.common.view

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class RememberAnnotatedHtmlStringTest {

    @Test
    fun `tokenize 1`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Whitespace,
                HtmlToken.Word("Hello"),
                HtmlToken.Whitespace,
                HtmlToken.Word("World!"),
                HtmlToken.Whitespace,
            ),
            actual = tokenizeHtml("  Hello  \n  World!  ").toList(),
        )
    }

    @Test
    fun `tokenize 2`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Whitespace,
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
                HtmlToken.Whitespace,
            ),
            actual = tokenizeHtml("<u>Hello</u>  ").toList(),
        )
    }

    @Test
    fun `tokenize 4`() {
        assertEquals(
            expected = listOf(
                HtmlToken.Tag("<u", emptyList(), ">"),
                HtmlToken.Whitespace,
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
                HtmlToken.Tag("<u", listOf(TagToken.Word("a"), TagToken.Equal, TagToken.Word("b")), ">"),
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
                HtmlToken.Tag("<u", listOf(TagToken.Word("a"), TagToken.Equal, TagToken.Quote("b")), ">"),
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
                HtmlToken.Tag("<u", listOf(TagToken.Word("a"), TagToken.Equal, TagToken.Quote("=b")), ">"),
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
                HtmlToken.Whitespace,
                HtmlToken.Tag("<u", emptyList(), ">"),
                HtmlToken.Word("Hello"),
                HtmlToken.Tag("</u", emptyList(), ">"),
            ),
            actual = tokenizeHtml("""  <u>Hello</u>""").toList(),
        )
    }

    @Test
    fun `consolidate white space between words outside of tag`() {
        assertEquals(
            expected = buildAnnotatedString { append("Hello World!") },
            actual = annotateHtmlString("  Hello  \n  World!  ", null, 12.sp),
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
            actual = annotateHtmlString("  <u>Hello</u>", null, 12.sp),
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
            actual = annotateHtmlString("<u>Hello</u>  ", null, 12.sp),
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
            actual = annotateHtmlString("<u>  Hello</u>", null, 12.sp),
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
            actual = annotateHtmlString("Hello  <u>World!</u>", null, 12.sp),
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
            actual = annotateHtmlString("Hello<u>  World!</u>", null, 12.sp),
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
            actual = annotateHtmlString("Hello  <u>  World!</u>", null, 12.sp),
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
            actual = annotateHtmlString("<u>Hello</u>  World!", null, 12.sp),
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
            actual = annotateHtmlString("<u>Hello  </u>World!", null, 12.sp),
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
            actual = annotateHtmlString("<u>Hello  </u>  World!", null, 12.sp),
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
            actual = annotateHtmlString("<u>Hello  World!</u>  ", null, 12.sp),
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
            actual = annotateHtmlString("<u>  Hello  World!</u>", null, 12.sp),
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
            actual = annotateHtmlString("  <u>  Hello World!</u>  ", null, 12.sp),
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
            actual = annotateHtmlString("<u>Hello  World!</u>  ", null, 12.sp),
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
            actual = annotateHtmlString("""<u>Hello  World!</u>""", null, 12.sp),
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
            actual = annotateHtmlString("""H<u>ello  Worl</u>d!""", null, 12.sp),
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
            actual = annotateHtmlString("""H  <u>ello  Worl</u>d!""", null, 12.sp),
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
            actual = annotateHtmlString("""H<u>ello  Worl</u>  d!""", null, 12.sp),
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
            actual = annotateHtmlString("""H<u>ello  Worl</u>  d!  """, null, 12.sp),
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
            actual = annotateHtmlString("""H<u>ello  Worl  </u>  d!""", null, 12.sp),
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
            actual = annotateHtmlString("""  <u>  <s>  Hello  </s>World</u>  """, null, 12.sp),
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
                """  Hello  <u>  World  <s>  Kotlin  </s>  </u>  """,
                null,
                12.sp,
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
                """  <u>  <s>  Hello  </s>  World  </u>  Kotlin  """,
                null,
                12.sp,
            ),
        )
    }
    //H<u>ello Worl </u> d!

    @Test
    fun `unescape ampersand`() {
        assertEquals(
            expected = buildAnnotatedString { append("&") },
            actual = annotateHtmlString("""&amp;""", null, 12.sp),
        )
    }

    @Test
    fun `unescape less than`() {
        assertEquals(
            expected = buildAnnotatedString { append("<") },
            actual = annotateHtmlString("""&lt;""", null, 12.sp),
        )
    }

    @Test
    fun `unescape greater than`() {
        assertEquals(
            expected = buildAnnotatedString { append(">") },
            actual = annotateHtmlString("""&gt;""", null, 12.sp),
        )
    }

    @Test
    fun `unescape quote`() {
        assertEquals(
            expected = buildAnnotatedString { append("\"") },
            actual = annotateHtmlString("""&quot;""", null, 12.sp),
        )
    }

    @Test
    fun `unescape apostrophe`() {
        assertEquals(
            expected = buildAnnotatedString { append("'") },
            actual = annotateHtmlString("""&#39;""", null, 12.sp),
        )
    }

    @Test
    fun `unescape two characters in a row`() {
        assertEquals(
            expected = buildAnnotatedString { append("<>") },
            actual = annotateHtmlString("""&lt;&gt;""", null, 12.sp),
        )
    }
}
