package com.monoid.hackernews.common.data.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.monoid.hackernews.common.room.BestStoryDb
import com.monoid.hackernews.common.room.HNDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class BestStoryDaoTest {
    private lateinit var database: HNDatabase

    @Before
    fun initDb() {
        database = Room
            .inMemoryDatabaseBuilder(getApplicationContext(), HNDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun upsertAndGetBestStories() = runTest {
        val bestStoryDb = listOf(BestStoryDb(itemId = 1, order = 0))
        database.bestStoryDao().upsertBestStories(bestStoryDb)

        assertEquals(
            expected = bestStoryDb,
            actual = database.bestStoryDao().getBestStories().first()
        )
    }

    @Test
    fun deleteBestStories() = runTest {
        val bestStoryDb = listOf(BestStoryDb(itemId = 1, order = 0))
        database.bestStoryDao().upsertBestStories(bestStoryDb)
        database.bestStoryDao().deleteBestStories()

        assertEquals(
            expected = emptyList(),
            actual = database.bestStoryDao().getBestStories().first()
        )
    }

    @Test
    fun replaceBestStories() = runTest {
        database.bestStoryDao().upsertBestStories(listOf(BestStoryDb(itemId = 1, order = 0)))
        val bestStoryDb = listOf(BestStoryDb(itemId = 2, order = 0))
        database.bestStoryDao().replaceBestStories(bestStoryDb)

        assertEquals(
            expected = bestStoryDb,
            actual = database.bestStoryDao().getBestStories().first()
        )
    }
}
