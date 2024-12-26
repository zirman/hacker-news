package com.monoid.hackernews.common.data

import org.koin.core.annotation.Module

internal const val databaseFileName = "hn_database.db"

@Module(includes = [DatabaseDaoModule::class])
expect class DatabaseModule
