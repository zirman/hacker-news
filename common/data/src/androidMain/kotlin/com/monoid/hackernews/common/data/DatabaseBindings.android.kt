package com.monoid.hackernews.common.data

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.core.metro.IoDispatcherQualifier
import com.monoid.hackernews.common.core.metro.ProcessLifecycleOwnerQualifier
import com.monoid.hackernews.common.data.room.HNDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineDispatcher

@ContributesTo(AppScope::class)
@BindingContainer(includes = [DatabaseDaoBindings::class])
object AndroidDatabaseBindings {
    @Provides
    fun providesHnDatabase(
        context: Context,
        @IoDispatcherQualifier
        coroutineDispatcher: CoroutineDispatcher,
        @ProcessLifecycleOwnerQualifier
        lifecycleOwner: LifecycleOwner,
    ): HNDatabase {
        val database = Room
            .databaseBuilder<HNDatabase>(
                context = context,
                name = context.getDatabasePath(DATABASE_FILE_NAME).absolutePath,
                // factory = { HNDatabase::class.instantiateImpl() },
            )
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                database.close()
            }
        })
        return database
    }
}
