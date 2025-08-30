package com.monoid.hackernews.common.data

import org.koin.core.annotation.Module

internal const val DATABASE_FILE_NAME = "hn_database.db"

@Module(includes = [DatabaseDaoModule::class])
expect class DatabaseModule
