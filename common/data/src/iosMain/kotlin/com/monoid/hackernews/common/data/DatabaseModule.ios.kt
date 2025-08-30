@file:OptIn(ExperimentalForeignApi::class)

package com.monoid.hackernews.common.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.core.IoDispatcherQualifier
import com.monoid.hackernews.common.data.room.HNDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Module(includes = [DatabaseDaoModule::class])
actual class DatabaseModule {

    @Single
    fun hnDatabase(
        @Named(type = IoDispatcherQualifier::class)
        coroutineDispatcher: CoroutineDispatcher,
    ): HNDatabase {
        val documentDirectory = NSFileManager.defaultManager
            .URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            ?.path
            .let { checkNotNull(it) }
        Room
            .databaseBuilder<HNDatabase>(name = "$documentDirectory/$DATABASE_FILE_NAME")
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
            .run { return this }
    }
}
