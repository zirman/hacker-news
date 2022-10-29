package com.monoid.hackernews.shared.data

import android.content.Context
import com.monoid.hackernews.shared.navigation.Username
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.api.getFavorites
import com.monoid.hackernews.shared.room.FavoriteDao
import com.monoid.hackernews.shared.settingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class FavoriteStoryRepository(
    private val context: Context,
    private val favoriteDao: FavoriteDao,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
        return context.settingsDataStore.data
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

    override suspend fun updateItems() {
        val authentication = context.settingsDataStore.data.first()

        if (authentication.password?.isNotEmpty() == true) {
            favoriteDao.replaceFavoritesForUser(
                username = authentication.username,
                favorites = getFavorites(context, Username(authentication.username)).map { it.long },
            )
        }
    }
}
