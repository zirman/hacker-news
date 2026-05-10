package com.monoid.hackernews.common.data.model

import androidx.datastore.core.CorruptionException
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json

actual var MutablePreferences.settings: Settings?
    get() = try {
        get(SETTINGS_KEY)
            ?.decodeToString()
            ?.let { Json.decodeFromString<Settings>(it) }
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot read preferences", throwable)
    }
    set(value) {
        set(
            SETTINGS_KEY,
            Json.encodeToString(Settings.serializer(), checkNotNull(value))
                .toByteArray(),
        )
    }

actual val Preferences.settings: Settings?
    get() = try {
        get(SETTINGS_KEY)
            ?.decodeToString()
            ?.let { Json.decodeFromString<Settings>(it) }
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot read preferences", throwable)
    }
