@file:OptIn(ExperimentalCoroutinesApi::class)

package com.monoid.hackernews.common.data

import androidx.datastore.core.DataStore
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getFavorites
import com.monoid.hackernews.common.room.FavoriteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class FavoriteStoryRepository(
    private val authentication: DataStore<Authentication>,
    private val favoriteDao: FavoriteDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = authentication.data
        .map { authentication ->
            if (authentication.password.isNotEmpty()) {
                authentication.username
            } else {
                ""
            }
        }
        .distinctUntilChanged()
        .flatMapLatest { username -> favoriteDao.getFavoritesForUser(username) }
        .map { favoriteStories ->
            favoriteStories.mapIndexed { index, favorite ->
                OrderedItem(
                    itemId = ItemId(favorite.itemId),
                    order = index
                )
            }
        }
        .shareIn(
            scope = scope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    override suspend fun updateItems() {
        val authentication = authentication.data.first()

        if (authentication.password.isNotEmpty()) {
            favoriteDao.replaceFavoritesForUser(
                username = authentication.username,
                favorites = getFavorites(Username(authentication.username)).map { it.long }
            )
        }
    }
}
