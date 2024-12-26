package com.monoid.hackernews.common.data.model

import androidx.datastore.core.CorruptionException
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences

actual var MutablePreferences.settings: Settings?
    get() = try {
        TODO()
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot read preferences", throwable)
    }
    set(value) = try {
        TODO()
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot write preferences", throwable)
    }

actual val Preferences.settings: Settings?
    get() = try {
        TODO()
    } catch (throwable: Throwable) {
        throw CorruptionException("Cannot read preferences", throwable)
    }
