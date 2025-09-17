package com.monoid.hackernews.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okio.Path.Companion.toPath
import java.io.File

@ContributesTo(AppScope::class)
@BindingContainer
object JvmDataStoreBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun providesDataStorePreferences(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                File(DATA_STORE_FILE_NAME).absolutePath.toPath()
            },
        )
    }
}
