package com.monoid.hackernews.common.data.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.api.commentRequest
import com.monoid.hackernews.common.data.room.CommentDao
import com.monoid.hackernews.common.data.room.CommentDb
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@Inject
class CommentRepository(
    private val logger: LoggerAdapter,
    private val remoteDataSource: HttpClient,
    private val localDataSource: CommentDao,
    private val localPreferencesDataSource: DataStore<Preferences>,
) {
    private val jobs = mutableMapOf<ItemId, Job>()

    private val scope = CoroutineScope(
        SupervisorJob() +
            CoroutineExceptionHandler { _, throwable ->
                logger.recordException(
                    messageString = "CoroutineExceptionHandler",
                    throwable = throwable,
                    tag = TAG,
                )
            },
    )

    fun commentFlow(parentId: ItemId): Flow<CommentDb?> = flow {
        if (localDataSource.getComment(parentId.long) == null) {
            localDataSource.commentUpsert(CommentDb(parentId.long, ""))
        }
        emitAll(localDataSource.commentFlow(parentId.long))
    }

    suspend fun comment(parentId: ItemId): CommentDb {
        var x = localDataSource.getComment(parentId.long)
        if (x == null) {
            x = CommentDb(parentId.long, "")
            localDataSource.commentUpsert(x)
        }
        return x
    }

    suspend fun updateComment(parentId: ItemId, string: String) {
        localDataSource.commentUpsert(CommentDb(parentId.long, string))
    }

    // Main used to synchronize access to jobs
    suspend fun sendComment(parentId: ItemId): Unit = withContext(Dispatchers.Main.immediate) {
        jobs
            .getOrPut(parentId) {
                scope.launch {
                    try {
                        val comment = localDataSource.getComment(parentId.long) ?: return@launch
                        remoteDataSource.commentRequest(
                            settings = localPreferencesDataSource.data
                                .map { it.settings }
                                .filterNotNull()
                                .first(),
                            parentId = parentId,
                            text = comment.text,
                        )
                        localDataSource.deleteComment(parentId.long)
                    } finally {
                        jobs.remove(parentId)
                    }
                }
            }
            .join()
    }
}

private const val TAG = "CommentRepository"
