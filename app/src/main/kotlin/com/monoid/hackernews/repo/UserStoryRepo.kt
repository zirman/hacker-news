package com.monoid.hackernews.repo

import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getUser
import com.monoid.hackernews.api.toRoomUser
import com.monoid.hackernews.room.Item
import com.monoid.hackernews.room.ItemDao
import com.monoid.hackernews.room.UserDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.CancellationException

class UserStoryRepo(
    private val httpClient: HttpClient,
    private val userDao: UserDao,
    private val itemDao: ItemDao,
    private val username: Username,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
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

    override suspend fun updateRepoItems() {
        try {
            val user = httpClient.getUser(username = username)

            itemDao.insertIdsIgnore(
                user.submitted.map { Item(id = it.long, by = username.string) }
            )

            userDao.insertReplace(user.toRoomUser())
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
        }
    }
}
