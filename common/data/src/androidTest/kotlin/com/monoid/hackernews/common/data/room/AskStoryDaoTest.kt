package com.monoid.hackernews.common.data.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.monoid.hackernews.common.room.AskStoryDb
import com.monoid.hackernews.common.room.HNDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AskStoryDaoTest {
    private lateinit var database: HNDatabase

    @Before
    fun initDb() {
        database = Room
            .inMemoryDatabaseBuilder(getApplicationContext(), HNDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun upsertAndGetAskStories() = runTest {
        val askStoryDb = listOf(AskStoryDb(itemId = 1, order = 0))
        database.askStoryDao().upsertAskStories(askStoryDb)

        assertEquals(
            expected = askStoryDb,
            actual = database.askStoryDao().getAskStories().first()
        )
    }

    @Test
    fun deleteAskStories() = runTest {
        val askStoryDb = listOf(AskStoryDb(itemId = 1, order = 0))
        database.askStoryDao().upsertAskStories(askStoryDb)
        database.askStoryDao().deleteAskStories()

        assertEquals(
            expected = emptyList(),
            actual = database.askStoryDao().getAskStories().first()
        )
    }

    @Test
    fun replaceAskStories() = runTest {
        database.askStoryDao().upsertAskStories(listOf(AskStoryDb(itemId = 1, order = 0)))
        val askStoryDb = listOf(AskStoryDb(itemId = 2, order = 0))
        database.askStoryDao().replaceAskStories(askStoryDb)

        assertEquals(
            expected = askStoryDb,
            actual = database.askStoryDao().getAskStories().first()
        )
    }
}
