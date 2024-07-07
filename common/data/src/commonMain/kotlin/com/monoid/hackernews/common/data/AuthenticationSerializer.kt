@file:OptIn(ExperimentalSerializationApi::class)

package com.monoid.hackernews.common.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

object AuthenticationSerializer : Serializer<Preferences> {
    override val defaultValue: Preferences = Preferences()
    override suspend fun readFrom(input: InputStream): Preferences = try {
        Json.decodeFromStream<Preferences>(input)
    } catch (throwable: Throwable) {
        currentCoroutineContext().ensureActive()
        throw CorruptionException("Cannot read authentication.", throwable)
    }

    override suspend fun writeTo(t: Preferences, output: OutputStream) {
        Json.encodeToStream(t, output)
    }
}
