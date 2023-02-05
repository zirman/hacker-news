package com.monoid.hackernews.common.data

import androidx.datastore.core.DataStore
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getUser
import com.monoid.hackernews.common.api.toUserApiUpdate
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.room.ItemDao
import com.monoid.hackernews.common.room.ItemDb
import com.monoid.hackernews.common.room.UserDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStoryRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val authentication: DataStore<Authentication>,
    private val userDao: UserDao,
    private val itemDao: ItemDao,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
        return authentication.data
            .map { it.username }
            .distinctUntilChanged()
            .flatMapLatest { userDao.userByUsernameWithSubmitted(it) }
            .map { userWithSubmitted ->
                userWithSubmitted?.submitted
                    ?.asReversed()
                    ?.mapIndexed { index, item ->
                        OrderedItem(
                            itemId = ItemId(item.id),
                            order = index
                        )
                    }
                    ?: emptyList()
            }
    }

    override suspend fun updateItems() {
        val username = Username(authentication.data.first().username)
        val user = httpClient.getUser(username = username)
        itemDao.itemsInsert(user.submitted.map { ItemDb(id = it.long, by = username.string) })
        userDao.insertReplace(user.toUserApiUpdate())
    }
}
