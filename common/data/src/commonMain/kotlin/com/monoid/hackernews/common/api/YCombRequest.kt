package com.monoid.hackernews.common.api

import com.monoid.hackernews.common.data.Authentication
import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ParametersBuilder

expect suspend fun HttpClient.yCombRequest(
    authentication: Authentication?,
    path: String,
    parametersBuilder: ParametersBuilder.() -> Unit = {},
): HttpResponse
