package com.monoid.hackernews.data

import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getUser
import com.monoid.hackernews.api.toUserApiUpdate
import com.monoid.hackernews.room.ItemDao
import com.monoid.hackernews.room.ItemDb
import com.monoid.hackernews.room.UserDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStoryRepository(
    private val httpClient: HttpClient,
    private val userDao: UserDao,
    private val itemDao: ItemDao,
    private val username: Username,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
        return userDao.userByUsernameWithSubmitted(username.string)
            .map { userWithSubmitted ->
                userWithSubmitted?.submitted
                    ?.asReversed()
                    ?.mapIndexed { index, item ->
                        OrderedItem(
                            itemId = ItemId(item.id),
                            order = index,
                        )
                    }
                    ?: emptyList()
            }
    }

    override suspend fun updateItems() {
        val user = httpClient.getUser(username = username)
        itemDao.itemsInsert(user.submitted.map { ItemDb(id = it.long, by = username.string) })
        userDao.insertReplace(user.toUserApiUpdate())
    }
}
