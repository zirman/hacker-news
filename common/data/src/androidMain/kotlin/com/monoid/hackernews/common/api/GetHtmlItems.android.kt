package com.monoid.hackernews.common.api

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("SetJavaScriptEnabled")
actual suspend fun getHtmlItems(
    path: String,
): List<ItemId> = suspendCoroutine { _ ->
    // TODO: make platform agnostic
//    val wv = WebView(context)
//    wv.settings.javaScriptEnabled = true
//
//    // prevent resuming continuation more than once
//    var hasResumed = false
//
//    wv.webViewClient =
//        object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String?) {
//                super.onPageFinished(view, url)
//
//                if (hasResumed.not()) {
//                    hasResumed = true
//
//                    view.evaluateJavascript(
//                        """Array.from(document.getElementsByClassName("athing")).map(e => e.id)"""
//                    ) { favoritesString ->
//                        continuation.resume(
//                            (Json.parseToJsonElement(favoritesString) as JsonArray)
//                                .map { ItemId((it as JsonPrimitive).content.toLong()) }
//                        )
//                    }
//                }
//            }
//
//            override fun onReceivedHttpError(
//                view: WebView,
//                request: WebResourceRequest,
//                errorResponse: WebResourceResponse,
//            ) {
//                super.onReceivedHttpError(view, request, errorResponse)
//
//                if (hasResumed.not()) {
//                    hasResumed = true
//
//                    continuation.resumeWithException(WebViewException())
//                }
//            }
//
//            override fun onReceivedSslError(
//                view: WebView,
//                handler: SslErrorHandler,
//                error: SslError,
//            ) {
//                super.onReceivedSslError(view, handler, error)
//
//                if (hasResumed.not()) {
//                    hasResumed = true
//
//                    continuation.resumeWithException(WebViewException())
//                }
//            }
//        }
//
//    wv.loadUrl("$yCombinatorBaseUrl/$path")
}
