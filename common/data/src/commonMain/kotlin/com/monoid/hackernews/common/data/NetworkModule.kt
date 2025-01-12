package com.monoid.hackernews.common.data

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
expect class NetworkModule {

    @Single
    fun json(): Json

    @Single
    fun httpClient(json: Json): HttpClient
}
