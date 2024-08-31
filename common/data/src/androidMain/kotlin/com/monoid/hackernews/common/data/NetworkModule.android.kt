package com.monoid.hackernews.common.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

actual val networkModule: Module = module {
    single<HttpClient> {
        HttpClient(Android) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.NONE // TODO debug builds
            }

            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }
}
