package com.monoid.hackernews.common.data.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.monoid.hackernews.common.room.HNDatabase
import com.monoid.hackernews.common.room.ShowStoryDb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ShowStoryDaoTest {
    private lateinit var database: HNDatabase

    @Before
    fun initDb() {
        database = Room
            .inMemoryDatabaseBuilder(getApplicationContext(), HNDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun upsertAndGetShowStories() = runTest {
        val showStoryDb = listOf(ShowStoryDb(itemId = 1, order = 0))
        database.showStoryDao().upsertShowStories(showStoryDb)

        assertEquals(
            expected = showStoryDb,
            actual = database.showStoryDao().getShowStories().first()
        )
    }

    @Test
    fun deleteShowStories() = runTest {
        val showStoryDb = listOf(ShowStoryDb(itemId = 1, order = 0))
        database.showStoryDao().upsertShowStories(showStoryDb)
        database.showStoryDao().deleteShowStories()

        assertEquals(
            expected = emptyList(),
            actual = database.showStoryDao().getShowStories().first()
        )
    }

    @Test
    fun replaceShowStories() = runTest {
        database.showStoryDao().upsertShowStories(listOf(ShowStoryDb(itemId = 1, order = 0)))
        val showStoryDb = listOf(ShowStoryDb(itemId = 2, order = 0))
        database.showStoryDao().replaceShowStories(showStoryDb)

        assertEquals(
            expected = showStoryDb,
            actual = database.showStoryDao().getShowStories().first()
        )
    }
}
