@file:OptIn(ExperimentalWasmJsInterop::class)

package com.monoid.hackernews.common.data

import androidx.room3.Room
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import com.monoid.hackernews.common.core.metro.IoDispatcherQualifier
import com.monoid.hackernews.common.data.room.HNDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineDispatcher
import org.w3c.dom.Worker

@ContributesTo(AppScope::class)
@BindingContainer(includes = [DatabaseDaoBindings::class])
object WasmDatabaseBindings {
    @Provides
    fun providesHnDatabase(
        @IoDispatcherQualifier
        coroutineDispatcher: CoroutineDispatcher,
    ): HNDatabase {
        val database = Room
            .databaseBuilder<HNDatabase>(name = DATABASE_FILE_NAME)
            .fallbackToDestructiveMigration(true)
            .setDriver(WebWorkerSQLiteDriver(newWorker()))
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
        return database
    }
}

private fun newWorker(): Worker =
    js("""new Worker(new URL("sql-js-worker/worker.js", import.meta.url), { type: "module" })""")
