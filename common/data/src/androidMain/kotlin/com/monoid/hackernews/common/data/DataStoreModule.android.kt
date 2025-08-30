package com.monoid.hackernews.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
actual class DataStoreModule {

    @Single
    fun dataStorePreferences(
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
