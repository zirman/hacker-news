package com.monoid.hackernews

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner as P
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationLifecycleOwner

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @ApplicationLifecycleOwner
    @Provides
    fun provideApplicationLifecycleOwner(): LifecycleOwner {
        return P.get()
    }
}
