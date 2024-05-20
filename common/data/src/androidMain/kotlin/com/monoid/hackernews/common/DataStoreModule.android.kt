package com.monoid.hackernews.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.monoid.hackernews.common.data.AuthenticationSerializer
import com.monoid.hackernews.common.data.Preferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dataStoreModule: Module = module {
    single {
        androidContext().dataStore
//        DataStore<Authentication>.create {
//            androidContext()
//                .filesDir.resolve(dataStoreFileName)
//                .absolutePath.toPath()
//        }
    }
}

private val Context.dataStore: DataStore<Preferences> by dataStore(
    fileName = "settings.pb",
    serializer = AuthenticationSerializer,
)
