package com.monoid.hackernews.common.data.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.monoid.hackernews.common.room.ExpandedDb
import com.monoid.hackernews.common.room.HNDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpandedDaoTest {
    private lateinit var database: HNDatabase

    @Before
    fun initDb() {
        database = Room
            .inMemoryDatabaseBuilder(getApplicationContext(), HNDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun insertAndIsExpanded() = runTest {
        val itemId = 1L
        database.expandedDao().expandedInsert(ExpandedDb(itemId = itemId))

        assertTrue(
            actual = database.expandedDao().isExpanded(itemId)
        )
    }

    @Test
    fun insertAndIsExpandedFlow() = runTest {
        val itemId = 1L
        database.expandedDao().expandedInsert(ExpandedDb(itemId = itemId))

        assertTrue(
            actual = database.expandedDao().isExpandedFlow(itemId).first()
        )
    }

    @Test
    fun deleteIsExpanded() = runTest {
        val itemId = 1L
        val expandedDb = ExpandedDb(itemId = itemId)
        database.expandedDao().expandedInsert(expandedDb)
        database.expandedDao().expandedDelete(expandedDb)

        assertFalse(
            actual = database.expandedDao().isExpanded(itemId)
        )
    }
}
