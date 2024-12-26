@file:OptIn(ExperimentalSerializationApi::class)

package com.monoid.hackernews.common.data.model

import androidx.datastore.core.CorruptionException
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayOutputStream

actual var MutablePreferences.settings: Settings?
    get() = try {
        get(SETTINGS_KEY)
            ?.inputStream()
            ?.use { Json.decodeFromStream<Settings>(it) }
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot read preferences", throwable)
    }
    set(value) = try {
        ByteArrayOutputStream().use {
            Json.encodeToStream(value, it)
            set(SETTINGS_KEY, it.toByteArray())
        }
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot write preferences", throwable)
    }

actual val Preferences.settings: Settings?
    get() = try {
        get(SETTINGS_KEY)
            ?.inputStream()
            ?.use { Json.decodeFromStream<Settings>(it) }
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot read preferences", throwable)
    }
