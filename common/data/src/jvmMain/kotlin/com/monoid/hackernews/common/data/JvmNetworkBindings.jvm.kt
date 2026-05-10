package com.monoid.hackernews.common.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@ContributesTo(AppScope::class)
@BindingContainer
object JvmNetworkBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun providesJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @SingleIn(AppScope::class)
    @Provides
    fun providesHttpClient(json: Json): HttpClient = HttpClient(Apache5) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }

        install(ContentNegotiation) { json(json) }

        engine {
            followRedirects = true
            socketTimeout = 10_000
            connectTimeout = 10_000
            connectionRequestTimeout = 20_000
        }
    }
}
