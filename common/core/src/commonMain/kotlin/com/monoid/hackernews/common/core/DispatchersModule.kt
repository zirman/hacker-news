package com.monoid.hackernews.common.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO // needed import on iOS
import kotlinx.coroutines.MainCoroutineDispatcher
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
class DispatchersModule {

    @Single
    fun dispatchersMain(): MainCoroutineDispatcher = Dispatchers.Main

    @Single
    @Named(type = DefaultDispatcherQualifier::class)
    fun dispatchersDefault(): CoroutineDispatcher = Dispatchers.Default

    @Single
    @Named(type = IoDispatcherQualifier::class)
    fun dispatchersIo(): CoroutineDispatcher = Dispatchers.IO

    @Single
    @Named(type = UnconfinedDispatcherQualifier::class)
    fun dispatchersUnconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}
