package com.monoid.hackernews.common.data

import org.koin.core.annotation.Module

internal const val dataStoreFileName = "settings.preferences_pb"

@Module
expect class DataStoreModule
