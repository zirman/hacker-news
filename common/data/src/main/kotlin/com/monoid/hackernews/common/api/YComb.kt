package com.monoid.hackernews.common.api

import android.annotation.SuppressLint
import android.content.Context
import android.net.http.SslError
import android.text.Html
import android.text.Spanned
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.text.getSpans
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.datastore.Authentication
import io.ktor.client.HttpClient
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.contentType
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class YCombException(message: String? = null) : Exception(message)
class WebViewException : Exception()

private const val baseUrl = "https://news.ycombinator.com"

private suspend inline fun HttpClient.yCombRequest(
    authentication: Authentication?,
    path: String,
    parametersBuilder: ParametersBuilder.() -> Unit = {},
): HttpResponse {
    val httpResponse: HttpResponse = submitForm(
        url = "$baseUrl/$path",
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
                httpResponse.bodyAsText()

            try {
                val spanned: Spanned =
                    Html.fromHtml(content, 0)

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
                currentCoroutineContext().ensureActive()
                YCombException()
            }
        } else {
            YCombException()
        }
    }

    return httpResponse
}

@SuppressLint("SetJavaScriptEnabled")
suspend fun getHtmlItems(
    context: Context,
    path: String,
): List<ItemId> = suspendCoroutine { continuation ->
    // TODO: reuse WebView instance
    val wv = WebView(context)
    wv.settings.javaScriptEnabled = true

    // prevent resuming continuation more than once
    var hasResumed = false

    wv.webViewClient =
        object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)

                if (hasResumed.not()) {
                    hasResumed = true

                    view.evaluateJavascript(
                        """Array.from(document.getElementsByClassName("athing")).map(e => e.id)"""
                    ) { favoritesString ->
                        continuation.resume(
                            (Json.parseToJsonElement(favoritesString) as JsonArray)
                                .map { ItemId((it as JsonPrimitive).content.toLong()) }
                        )
                    }
                }
            }

            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                errorResponse: WebResourceResponse,
            ) {
                super.onReceivedHttpError(view, request, errorResponse)

                if (hasResumed.not()) {
                    hasResumed = true

                    continuation.resumeWithException(WebViewException())
                }
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError,
            ) {
                super.onReceivedSslError(view, handler, error)

                if (hasResumed.not()) {
                    hasResumed = true

                    continuation.resumeWithException(WebViewException())
                }
            }
        }

    wv.loadUrl("$baseUrl/$path")
}

suspend fun HttpClient.registerRequest(authentication: Authentication) {
    yCombRequest(
        authentication = authentication,
        path = "login",
    ) {
        append("creating", "t")
    }
}

suspend fun HttpClient.loginRequest(authentication: Authentication) {
    yCombRequest(
        authentication = authentication,
        path = "login",
    )
}

suspend fun HttpClient.favoriteRequest(
    authentication: Authentication,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        authentication = authentication,
        path = "fave",
    ) {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.flagRequest(
    authentication: Authentication,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        authentication = authentication,
        path = "flag",
    ) {
        append("id", itemId.long.toString())
        if (flag.not()) append("un", "t")
    }
}

suspend fun HttpClient.upvoteItem(
    authentication: Authentication,
    itemId: ItemId,
    flag: Boolean = true,
) {
    yCombRequest(
        authentication = authentication,
        path = "vote",
    ) {
        append("id", itemId.long.toString())
        append("how", if (flag) "up" else "un")
    }
}

suspend fun HttpClient.commentRequest(
    authentication: Authentication,
    parentId: ItemId,
    text: String,
) {
    yCombRequest(
        authentication = authentication,
        path = "comment",
    ) {
        append("parent", parentId.long.toString())
        append("text", text)
    }
}

suspend fun getFavorites(context: Context, username: Username): List<ItemId> {
    return getHtmlItems(context, path = "favorites?id=${username.string}")
}

suspend fun getSubmissions(context: Context, username: Username): List<ItemId> {
    return getHtmlItems(context, path = "submitted?id=${username.string}")
}

suspend fun getComments(context: Context, username: Username): List<ItemId> {
    return getHtmlItems(context, path = "threads?id=${username.string}")
}

suspend fun getUpvoted(
    context: Context,
    authentication: Authentication,
    username: Username
): List<ItemId> {
    return getHtmlItems(
        context = context,
        path = "upvoted?id=${username.string}&acct=${authentication.username}&pw=${authentication.password}",
    )
}
