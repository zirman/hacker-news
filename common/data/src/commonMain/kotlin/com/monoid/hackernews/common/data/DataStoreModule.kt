package com.monoid.hackernews.common.data

import org.koin.core.annotation.Module

internal const val DATA_STORE_FILE_NAME = "settings.preferences_pb"

@Module
expect class DataStoreModule
