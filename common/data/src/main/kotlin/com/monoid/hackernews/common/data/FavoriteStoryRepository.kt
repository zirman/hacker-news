package com.monoid.hackernews.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.api.getFavorites
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.room.FavoriteDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteStoryRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val authentication: DataStore<Authentication>,
    private val favoriteDao: FavoriteDao,
) : Repository<OrderedItem> {
    override val items: Flow<List<OrderedItem>> = authentication.data
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
            scope = CoroutineScope(SupervisorJob()),
            started = SharingStarted.Lazily,
            replay = 1
        )

    override suspend fun updateItems() {
        val authentication = authentication.data.first()

        if (authentication.password?.isNotEmpty() == true) {
            favoriteDao.replaceFavoritesForUser(
                username = authentication.username,
                favorites = getFavorites(
                    context,
                    Username(authentication.username)
                ).map { it.long }
            )
        }
    }
}
