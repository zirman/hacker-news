package com.monoid.hackernews.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.monoid.hackernews.common.data.AuthenticationSerializer
import com.monoid.hackernews.common.datastore.Authentication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    fun provideAuthentication(
        @ApplicationContext
        context: Context,
    ): DataStore<Authentication> {
        return context.dataStore
    }

    private val Context.dataStore by dataStore(
        fileName = "settings.pb",
        serializer = AuthenticationSerializer,
    )
}
