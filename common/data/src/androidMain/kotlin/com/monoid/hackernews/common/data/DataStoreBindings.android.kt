package com.monoid.hackernews.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okio.Path.Companion.toPath

@ContributesTo(AppScope::class)
@BindingContainer
object AndroidDataStoreBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun providesDataStorePreferences(
        context: Context,
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                context
                    .filesDir.resolve(DATA_STORE_FILE_NAME)
                    .absolutePath.toPath()
            },
        )
    }
}
