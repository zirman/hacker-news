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
    private val preferencesDataSource: DataStore<Preferences>,
    private val favoriteLocalDataSource: FavoriteDao,
) : Repository<OrderedItem> {
    override fun getItems(scope: CoroutineScope): Flow<List<OrderedItem>> = preferencesDataSource.data
        .map { authentication ->
            if (authentication.password.string.isNotEmpty()) {
                authentication.username
            } else {
                Username("")
            }
        }
        .distinctUntilChanged()
        .flatMapLatest { username -> favoriteLocalDataSource.getFavoritesForUser(username.string) }
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
        val authentication = preferencesDataSource.data.first()

        if (authentication.password.string.isNotEmpty()) {
            favoriteLocalDataSource.replaceFavoritesForUser(
                username = authentication.username.string,
                favorites = getFavorites(Username(authentication.username.string)).map { it.long }
            )
        }
    }
}
