package com.monoid.hackernews.common.api

import android.text.Html
import android.text.Spanned
import androidx.core.text.getSpans
import com.monoid.hackernews.common.data.Authentication
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

actual suspend fun HttpClient.yCombRequest(
    authentication: Authentication?,
    path: String,
    parametersBuilder: ParametersBuilder.() -> Unit,
): HttpResponse {
    val httpResponse: HttpResponse = submitForm(
        url = "$yCombinatorBaseUrl/$path",
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
