package com.monoid.hackernews.common.data.api

import com.monoid.hackernews.common.data.model.Settings
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

suspend fun HttpClient.yCombRequest(
    path: String,
    settings: Settings? = null,
    httpStatusCode: HttpStatusCode = HttpStatusCode.OK,
    parametersBuilder: (ParametersBuilder.() -> Unit)? = null,
): HttpResponse {
    val httpResponse: HttpResponse = submitForm(
        url = "$Y_COMBINATOR_BASE_URL/$path",
        formParameters = Parameters.build {
            if (settings != null) {
                append("acct", settings.username.string)
                append("pw", settings.password.string)
            }
            parametersBuilder?.invoke(this)
        },
    ) { expectSuccess = false }
    if (httpResponse.status != httpStatusCode) {
        val contentType: ContentType? = httpResponse.contentType()
        throw if (contentType != null && contentType.match(ContentType.Text.Html)) {
            try {
                YCombException(message = getLoginErrorMessage(httpResponse.bodyAsText()))
            } catch (throwable: Throwable) {
                currentCoroutineContext().ensureActive()
                YCombException(cause = throwable)
            }
        } else {
            YCombException()
        }
    }

    return httpResponse
}
