package com.monoid.hackernews.common

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dataStoreModule: Module = module {
    single {
        PreferenceDataStoreFactory.createWithPath {
            "preferences.db".toPath()
        }
    }
}
