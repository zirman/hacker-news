package com.monoid.hackernews.repo

import com.monoid.hackernews.HNApplication
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.room.FavoriteDao
import com.monoid.hackernews.settingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class FavoriteStoryRepo(
    private val favoriteDao: FavoriteDao,
) : OrderedItemRepo {
    override fun getRepoItems(): Flow<List<OrderedItem>> {
        return HNApplication.instance.settingsDataStore.data
            .map { authentication ->
                if (authentication.password.isNotEmpty()) {
                    authentication.username
                } else {
                    ""
                }
            }
            .distinctUntilChanged()
            .flatMapLatest { username -> favoriteDao.getAll(username) }
            .map { favoriteStories ->
                favoriteStories.mapIndexed { index, favorite ->
                    OrderedItem(
                        itemId = ItemId(favorite.itemId),
                        order = index,
                    )
                }
            }
    }

    override suspend fun updateRepoItems() {
    }
}
