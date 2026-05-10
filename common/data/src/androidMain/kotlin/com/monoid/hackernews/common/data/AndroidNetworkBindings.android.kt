package com.monoid.hackernews.common.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.monoid.hackernews.common.core.metro.ProcessLifecycleOwnerQualifier
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@ContributesTo(AppScope::class)
@BindingContainer
object AndroidNetworkBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun providesJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @SingleIn(AppScope::class)
    @Provides
    fun providesHttpClient(
        json: Json,
        @ProcessLifecycleOwnerQualifier
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
