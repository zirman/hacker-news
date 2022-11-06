package com.monoid.hackernews.shared.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.monoid.hackernews.shared.datastore.Authentication
import java.io.InputStream
import java.io.OutputStream

object AuthenticationSerializer : Serializer<Authentication> {
    override val defaultValue: Authentication = Authentication.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Authentication {
        try {
            @Suppress("BlockingMethodInNonBlockingContext")
            return Authentication.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: Authentication,
        output: OutputStream
    ) {
        @Suppress("BlockingMethodInNonBlockingContext")
        t.writeTo(output)
    }
}

val Context.settingsDataStore: DataStore<Authentication> by dataStore(
    fileName = "settings.pb",
    serializer = AuthenticationSerializer
)
