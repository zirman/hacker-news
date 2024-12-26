package com.monoid.hackernews.common.data

import org.koin.core.annotation.Module

@Module(includes = [DatabaseDaoModule::class])
actual class DatabaseModule
