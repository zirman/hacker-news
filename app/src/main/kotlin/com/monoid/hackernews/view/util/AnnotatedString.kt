package com.monoid.hackernews.view.util

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.URLSpan
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans

private const val urlTag = "URL"

@Composable
fun rememberAnnotatedString(
    text: String,
    contentColor: Color = LocalContentColor.current,
    linkColor: Color = MaterialTheme.colorScheme.tertiary,
): AnnotatedString {
    return remember(text, contentColor, linkColor) {
        getAnnotatedString(
            text = text,
            contentColor = contentColor,
            linkColor = linkColor,
        )
    }
}

fun getAnnotatedString(
    text: String,
    contentColor: Color = Color.White,
    linkColor: Color = Color.Blue,
): AnnotatedString {
    val spanned: Spanned =
        HtmlCompat.fromHtml(text, 0)

    return buildAnnotatedString {
        val asString: String =
            spanned.trimEnd().toString()

        append(asString)

        addStyle(
            style = SpanStyle(
                color = contentColor,
            ),
            start = 0,
            end = asString.length,
        )

        spanned.getSpans<URLSpan>().forEach { urlSpan ->
            addStringAnnotation(
                tag = urlTag,
                annotation = urlSpan.url,
                start = spanned.getSpanStart(urlSpan),
                end = spanned.getSpanEnd(urlSpan),
            )
            addStyle(
                style = SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline,
                ),
                start = spanned.getSpanStart(urlSpan),
                end = spanned.getSpanEnd(urlSpan),
            )
        }

        spanned.getSpans<StyleSpan>().forEach { styleSpan ->
            when (styleSpan.style) {
                Typeface.NORMAL ->
                    SpanStyle(fontStyle = FontStyle.Normal)
                Typeface.ITALIC ->
                    SpanStyle(fontStyle = FontStyle.Italic)
                Typeface.BOLD ->
                    SpanStyle(fontWeight = FontWeight.Bold)
                Typeface.BOLD_ITALIC ->
                    SpanStyle(
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                    )
                else ->
                    null
            }?.let { spanStyle ->
                addStyle(
                    style = spanStyle,
                    start = spanned.getSpanStart(styleSpan),
                    end = spanned.getSpanEnd(styleSpan),
                )
            }
        }
    }
}

fun AnnotatedString.onClick(context: Context, offset: Int): Boolean {
    val annotation = getStringAnnotations(
        tag = urlTag,
        start = offset,
        end = offset,
    ).firstOrNull()

    return if (annotation != null) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item)))
            true
        } catch (error: Throwable) {
            false
        }
    } else {
        false
    }
}
