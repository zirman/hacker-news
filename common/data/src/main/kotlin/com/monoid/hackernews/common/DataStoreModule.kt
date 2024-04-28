package com.monoid.hackernews.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.monoid.hackernews.common.data.AuthenticationSerializer
import com.monoid.hackernews.common.datastore.Authentication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataStoreModule = module {
    single<DataStore<Authentication>> {
        androidContext().dataStore
    }
}

private val Context.dataStore: DataStore<Authentication> by dataStore(
    fileName = "settings.pb",
    serializer = AuthenticationSerializer,
)
