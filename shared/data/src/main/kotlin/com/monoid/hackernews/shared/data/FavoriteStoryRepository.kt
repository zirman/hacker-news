package com.monoid.hackernews.shared.data

import android.content.Context
import androidx.datastore.core.DataStore
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.api.getFavorites
import com.monoid.hackernews.shared.datastore.Authentication
import com.monoid.hackernews.shared.room.FavoriteDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteStoryRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val authentication: DataStore<Authentication>,
    private val favoriteDao: FavoriteDao,
) : Repository<OrderedItem> {
    override fun getItems(): Flow<List<OrderedItem>> {
        return authentication.data
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
        val authentication = authentication.data.first()

        if (authentication.password?.isNotEmpty() == true) {
            favoriteDao.replaceFavoritesForUser(
                username = authentication.username,
                favorites = getFavorites(
                    context,
                    Username(authentication.username)
                ).map { it.long },
            )
        }
    }
}
