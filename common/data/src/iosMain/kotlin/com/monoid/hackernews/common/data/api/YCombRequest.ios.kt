package com.monoid.hackernews.common.data.api

import com.monoid.hackernews.common.data.model.Settings
import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ParametersBuilder

actual suspend fun HttpClient.yCombRequest(
    settings: Settings?,
    path: String,
    parametersBuilder: ParametersBuilder.() -> Unit
): HttpResponse {
    TODO("Not yet implemented")
}
