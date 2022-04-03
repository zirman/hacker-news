package com.monoid.hackernews.api

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.core.text.getSpans
import com.monoid.hackernews.datastore.Authentication
import io.ktor.client.HttpClient
import io.ktor.client.features.expectSuccess
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException

class YCombException(message: String? = null) : Exception(message)

private suspend inline fun HttpClient.yCombRequest(
    authentication: Authentication?,
    path: String,
    parametersBuilder: ParametersBuilder.() -> Unit = {},
) {
    val httpResponse: HttpResponse = submitForm(
        url = "https://news.ycombinator.com/$path",
        formParameters = Parameters.build {
            if (authentication != null) {
                append("acct", authentication.username)
                append("pw", authentication.password)
            }
            parametersBuilder()
        },
    ) { expectSuccess = false }

    if (httpResponse.status != HttpStatusCode.Found) {
        val contentType: ContentType? =
            httpResponse.contentType()

        throw if (contentType != null && contentType.match(ContentType.Text.Html)) {
            val content: String =
                httpResponse.readText()

            try {
                val spanned: Spanned =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(content, 0)
                    } else {
                        @Suppress("DEPRECATION")
                        Html.fromHtml(content)
                    }

                val firstSpan = spanned.getSpans<Any>().reduceOrNull { acc, span ->
                    if (spanned.getSpanStart(acc) < spanned.getSpanStart(span)) {
                        acc
                    } else {
                        span
                    }
                }

                YCombException(
                    message = if (firstSpan != null) {
                        spanned.toString().take(spanned.getSpanStart(firstSpan))
                    } else {
                        spanned.toString()
                    }.trim(),
                )
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                YCombException()
            }
        } else {
            YCombException()
        }
    }
}

suspend fun HttpClient.registerRequest(authentication: Authentication) {
    yCombRequest(authentication = authentication, path = "login") {
        append("creating", "t")
    }
}

suspend fun HttpClient.loginRequest(authentication: Authentication) {
    yCombRequest(authentication = authentication, path = "login")
}

suspend fun HttpClient.favoriteRequest(
    authentication: Authentication,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(authentication = authentication, path = "fave") {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.flagRequest(
    authentication: Authentication,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(authentication = authentication, path = "flag") {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.upvoteRequest(
    authentication: Authentication,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(authentication = authentication, path = "vote") {
        append("id", itemId.long.toString())
        append("how", if (flag) "up" else "un")
    }
}

suspend fun HttpClient.commentRequest(
    authentication: Authentication,
    parentId: ItemId,
    text: String,
) {
    yCombRequest(authentication = authentication, path = "comment") {
        append("parent", parentId.long.toString())
        append("text", text)
    }
}
