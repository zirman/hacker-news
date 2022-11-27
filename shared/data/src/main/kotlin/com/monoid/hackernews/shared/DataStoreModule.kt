package com.monoid.hackernews.shared

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.monoid.hackernews.shared.data.AuthenticationSerializer
import com.monoid.hackernews.shared.datastore.Authentication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
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
