package com.monoid.hackernews.common.core.metro

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainCoroutineDispatcher

@BindingContainer
class DispatcherBindings() {
    @SingleIn(AppScope::class)
    @Provides
    fun providesDispatchersMain(): MainCoroutineDispatcher = Dispatchers.Main

    @SingleIn(AppScope::class)
    @Named("DefaultDispatcherQualifier")
    @Provides
    fun providesDispatchersDefault(): CoroutineDispatcher = Dispatchers.Default

    @SingleIn(AppScope::class)
    @Named("IoDispatcherQualifier")
    @Provides
    fun providesDispatchersIo(): CoroutineDispatcher = Dispatchers.IO

    @SingleIn(AppScope::class)
    @Named("UnconfinedDispatcherQualifier")
    @Provides
    fun providesDispatchersUnconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}
