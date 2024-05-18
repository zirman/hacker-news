@file:OptIn(ExperimentalSerializationApi::class)

package com.monoid.hackernews.common.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

object AuthenticationSerializer : Serializer<Authentication> {
    override val defaultValue: Authentication = Authentication()
    override suspend fun readFrom(input: InputStream): Authentication = try {
        Json.decodeFromStream<Authentication>(input)
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot read authentication.", throwable)
    }

    override suspend fun writeTo(t: Authentication, output: OutputStream) {
        Json.encodeToStream(t, output)
    }
}
