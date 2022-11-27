package com.monoid.hackernews.shared.view

import android.content.Intent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel

@Module
@InstallIn(ActivityRetainedComponent::class)
object NewIntentChannelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideNewIntentChannel(): Channel<Intent> {
        return Channel()
    }
}
