package com.monoid.hackernews.common.data.model

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences

// TODO
actual var MutablePreferences.settings: Settings?
    get() = Settings()
    set(value) {}

// TODO
actual val Preferences.settings: Settings?
    get() = Settings()
