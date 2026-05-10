package com.monoid.hackernews.common.data.model

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences

actual var MutablePreferences.settings: Settings?
    get() = TODO("Not yet implemented")
    set(value) {}
actual val Preferences.settings: Settings?
    get() = TODO("Not yet implemented")
