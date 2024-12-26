package com.monoid.hackernews.common.injection

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainCoroutineDispatcher
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
class DispatcherModule {

    @Single
    fun dispatchersMain(): MainCoroutineDispatcher = Dispatchers.Main

    @Single
    @Named(type = DispatcherQualifier.Default::class)
    fun dispatchersDefault(): CoroutineDispatcher = Dispatchers.Default

    @Single
    @Named(type = DispatcherQualifier.Io::class)
    fun dispatchersIo(): CoroutineDispatcher = Dispatchers.IO

    @Single
    @Named(type = DispatcherQualifier.Unconfined::class)
    fun dispatchersUnconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}
