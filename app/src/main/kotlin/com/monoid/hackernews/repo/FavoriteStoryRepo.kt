package com.monoid.hackernews.repo

import android.content.Context
import com.monoid.hackernews.HNApplication
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getFavorites
import com.monoid.hackernews.room.FavoriteDao
import com.monoid.hackernews.settingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class FavoriteStoryRepo(
    private val context: Context,
    private val favoriteDao: FavoriteDao,
) : OrderedItemRepo() {
    override fun getDbItems(): Flow<List<OrderedItem>> {
        return HNApplication.instance.settingsDataStore.data
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
                        order = index,
                    )
                }
            }
    }

    override suspend fun updateDbItems() {
        val authentication = context.settingsDataStore.data.first()

        if (authentication.password?.isNotEmpty() == true) {
            favoriteDao.replaceFavoritesForUser(
                username = authentication.username,
                favorites = getFavorites(Username(authentication.username)).map { it.long },
            )
        }
    }
}
