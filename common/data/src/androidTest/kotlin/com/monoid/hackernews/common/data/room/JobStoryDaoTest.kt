package com.monoid.hackernews.common.data.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.monoid.hackernews.common.room.JobStoryDb
import com.monoid.hackernews.common.room.HNDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class JobStoryDaoTest {
    private lateinit var database: HNDatabase

    @Before
    fun initDb() {
        database = Room
            .inMemoryDatabaseBuilder(getApplicationContext(), HNDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun upsertAndGetJobStories() = runTest {
        val jobStoryDb = listOf(JobStoryDb(itemId = 1, order = 0))
        database.jobStoryDao().upsertJobStories(jobStoryDb)

        assertEquals(
            expected = jobStoryDb,
            actual = database.jobStoryDao().getJobStories().first()
        )
    }

    @Test
    fun deleteJobStories() = runTest {
        val jobStoryDb = listOf(JobStoryDb(itemId = 1, order = 0))
        database.jobStoryDao().upsertJobStories(jobStoryDb)
        database.jobStoryDao().deleteJobStories()

        assertEquals(
            expected = emptyList(),
            actual = database.jobStoryDao().getJobStories().first()
        )
    }

    @Test
    fun replaceJobStories() = runTest {
        database.jobStoryDao().upsertJobStories(listOf(JobStoryDb(itemId = 1, order = 0)))
        val jobStoryDb = listOf(JobStoryDb(itemId = 2, order = 0))
        database.jobStoryDao().replaceJobStories(jobStoryDb)

        assertEquals(
            expected = jobStoryDb,
            actual = database.jobStoryDao().getJobStories().first()
        )
    }
}
