package com.monoid.hackernews.common.data.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.monoid.hackernews.common.room.HNDatabase
import com.monoid.hackernews.common.room.NewStoryDb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class NewStoryDaoTest {
    private lateinit var database: HNDatabase

    @Before
    fun initDb() {
        database = Room
            .inMemoryDatabaseBuilder(getApplicationContext(), HNDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun upsertAndGetNewStories() = runTest {
        val newStoryDb = listOf(NewStoryDb(itemId = 1, order = 0))
        database.newStoryDao().upsertNewStories(newStoryDb)

        assertEquals(
            expected = newStoryDb,
            actual = database.newStoryDao().getNewStories().first()
        )
    }

    @Test
    fun deleteNewStories() = runTest {
        val newStoryDb = listOf(NewStoryDb(itemId = 1, order = 0))
        database.newStoryDao().upsertNewStories(newStoryDb)
        database.newStoryDao().deleteNewStories()

        assertEquals(
            expected = emptyList(),
            actual = database.newStoryDao().getNewStories().first()
        )
    }

    @Test
    fun replaceNewStories() = runTest {
        database.newStoryDao().upsertNewStories(listOf(NewStoryDb(itemId = 1, order = 0)))
        val newStoryDb = listOf(NewStoryDb(itemId = 2, order = 0))
        database.newStoryDao().replaceNewStories(newStoryDb)

        assertEquals(
            expected = newStoryDb,
            actual = database.newStoryDao().getNewStories().first()
        )
    }
}
