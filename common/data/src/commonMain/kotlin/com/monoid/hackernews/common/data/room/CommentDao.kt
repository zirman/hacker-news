package com.monoid.hackernews.common.data.room

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comment WHERE parentId = :parentId LIMIT 1")
    fun commentFlow(parentId: Long): Flow<CommentDb>

    @Query("SELECT * FROM comment WHERE parentId = :parentId LIMIT 1")
    suspend fun getComment(parentId: Long): CommentDb?

    @Query("DELETE FROM comment WHERE parentId = :parentId")
    suspend fun deleteComment(parentId: Long)

    @Upsert(entity = CommentDb::class)
    suspend fun commentUpsert(reply: CommentDb)
}
