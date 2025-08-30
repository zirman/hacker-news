package com.monoid.hackernews.common.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
actual class NetworkModule {

    @Single
    fun json(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Single
    fun httpClient(
        json: Json,
        @Named(type = ProcessLifecycleOwner::class)
        lifecycleOwner: LifecycleOwner,
    ): HttpClient {
        val client = HttpClient(Android) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.NONE // TODO debug builds
            }

            install(ContentNegotiation) { json(json) }
        }
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                client.close()
            }
        })
        return client
    }
}
