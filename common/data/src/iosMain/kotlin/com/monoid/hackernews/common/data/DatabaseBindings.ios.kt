@file:OptIn(ExperimentalForeignApi::class)

package com.monoid.hackernews.common.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.core.metro.IoDispatcherQualifier
import com.monoid.hackernews.common.data.room.HNDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@ContributesTo(AppScope::class)
@BindingContainer(includes = [DatabaseDaoBindings::class])
object IosDatabaseBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun providesHnDatabase(
        @IoDispatcherQualifier
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
        return Room
            .databaseBuilder<HNDatabase>(name = "$documentDirectory/$DATABASE_FILE_NAME")
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
    }
}
