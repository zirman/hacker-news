package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getUser
import com.monoid.hackernews.common.api.toUserApiUpdate
import com.monoid.hackernews.common.room.ItemDao
import com.monoid.hackernews.common.room.ItemDb
import com.monoid.hackernews.common.room.UserDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStoryRepositoryFactory @Inject constructor(
    private val httpClient: HttpClient,
    private val userDao: UserDao,
    private val itemDao: ItemDao,
) {
    private val repositories: MutableMap<Username, Repository<OrderedItem>> =
        mutableMapOf()

    fun repository(username: Username): Repository<OrderedItem> {
        return repositories.getOrPut(username) {
            object : Repository<OrderedItem> {
                override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = userDao
                    .userByUsernameWithSubmitted(username.string)
                    .map { userWithSubmitted ->
                        userWithSubmitted?.submitted
                            ?.asReversed()
                            ?.mapIndexed { index, item ->
                                OrderedItem(
                                    itemId = ItemId(item.id),
                                    order = index
                                )
                            }
                            .orEmpty()
                    }
                    .shareIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        replay = 1
                    )

                override suspend fun updateItems() {
                    val user = httpClient.getUser(username = username)
                    itemDao.itemsInsert(user.submitted.map {
                        ItemDb(
                            id = it.long,
                            by = username.string
                        )
                    })
                    userDao.upsertReplace(user.toUserApiUpdate())
                }
            }
        }
    }
}
