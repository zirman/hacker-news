package com.monoid.hackernews.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.io.File

@Module
actual class DataStoreModule {

    @Single
    fun dataStorePreferences(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                File(DATA_STORE_FILE_NAME).absolutePath.toPath()
            },
        )
    }
}
