package com.monoid.hackernews.common.data.model

import androidx.datastore.core.CorruptionException
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual var MutablePreferences.settings: Settings?
    get() = try {
        get(SETTINGS_KEY)?.decodeToString()?.run { Json.decodeFromString<Settings>(this) }
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot read preferences", throwable)
    }
    set(value) = try {
        set(SETTINGS_KEY, Json.encodeToString(value).toByteArray())
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot write preferences", throwable)
    }

actual val Preferences.settings: Settings?
    get() = try {
        get(SETTINGS_KEY)
            ?.decodeToString()
            ?.run { Json.decodeFromString(this) }
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot read preferences", throwable)
    }
