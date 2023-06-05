package com.monoid.hackernews.common.data.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.monoid.hackernews.common.room.HNDatabase
import com.monoid.hackernews.common.room.TopStoryDb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class TopStoryDaoTest {
    private lateinit var database: HNDatabase

    @Before
    fun initDb() {
        database = Room
            .inMemoryDatabaseBuilder(getApplicationContext(), HNDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun upsertAndGetTopStories() = runTest {
        val topStoryDb = listOf(TopStoryDb(itemId = 1, order = 0))
        database.topStoryDao().upsertTopStories(topStoryDb)

        assertEquals(
            expected = topStoryDb,
            actual = database.topStoryDao().getTopStories().first()
        )
    }

    @Test
    fun deleteTopStories() = runTest {
        val topStoryDb = listOf(TopStoryDb(itemId = 1, order = 0))
        database.topStoryDao().upsertTopStories(topStoryDb)
        database.topStoryDao().deleteTopStories()

        assertEquals(
            expected = emptyList(),
            actual = database.topStoryDao().getTopStories().first()
        )
    }

    @Test
    fun replaceTopStories() = runTest {
        database.topStoryDao().upsertTopStories(listOf(TopStoryDb(itemId = 1, order = 0)))
        val topStoryDb = listOf(TopStoryDb(itemId = 2, order = 0))
        database.topStoryDao().replaceTopStories(topStoryDb)

        assertEquals(
            expected = topStoryDb,
            actual = database.topStoryDao().getTopStories().first()
        )
    }
}
