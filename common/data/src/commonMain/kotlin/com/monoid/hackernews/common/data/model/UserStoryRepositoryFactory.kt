package com.monoid.hackernews.common.data.model

import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.api.getUser
import com.monoid.hackernews.common.data.api.toUserApiUpdate
import com.monoid.hackernews.common.data.room.ItemDao
import com.monoid.hackernews.common.data.room.UserDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class UserStoryRepositoryFactory(
    private val remoteDataSource: HttpClient,
    private val userLocalDataSource: UserDao,
    private val itemLocalDataSource: ItemDao,
) {
    private val repositories: MutableMap<Username, Repository<OrderedItem>> =
        mutableMapOf()

    fun repository(username: Username): Repository<OrderedItem> {
        return repositories.getOrPut(username) {
            object : Repository<OrderedItem> {
                override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = userLocalDataSource
                    .userByUsernameWithSubmitted(username.string)
                    .map { userWithSubmitted ->
                        userWithSubmitted?.submitted
                            ?.asReversed()
                            ?.mapIndexed { index, item ->
                                OrderedItem(
                                    itemId = ItemId(item.id),
                                    order = index,
                                )
                            }
                            .orEmpty()
                    }
                    .shareIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        replay = 1,
                    )

                override suspend fun updateItems() {
                    val user = remoteDataSource.getUser(username = username)
//                    itemLocalDataSource.itemsInsert(user.submitted.map {
//                        ItemDb(
//                            id = it.long,
//                            by = username.string,
//                        )
//                    })
                    userLocalDataSource.upsertReplace(user.toUserApiUpdate())
                }
            }
        }
    }
}
