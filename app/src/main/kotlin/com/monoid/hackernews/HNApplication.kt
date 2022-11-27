package com.monoid.hackernews

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import androidx.datastore.core.DataStore
import com.monoid.hackernews.shared.api.getFavorites
import com.monoid.hackernews.shared.api.getUpvoted
import com.monoid.hackernews.shared.data.ItemTreeRepository
import com.monoid.hackernews.shared.data.Username
import com.monoid.hackernews.shared.datastore.Authentication
import com.monoid.hackernews.shared.room.FavoriteDao
import com.monoid.hackernews.shared.room.HNDatabase
import com.monoid.hackernews.shared.room.UpvoteDao
import com.monoid.hackernews.shared.view.updateAndPushDynamicShortcuts
import dagger.hilt.android.HiltAndroidApp
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class HNApplication : Application() {

    private val coroutineScope = CoroutineScope(
        Dispatchers.Main.immediate + CoroutineExceptionHandler { _, error ->
            error.printStackTrace()
        }
    )

    @Inject
    lateinit var authentication: DataStore<Authentication>

    @Inject
    lateinit var db: HNDatabase

    @Inject
    lateinit var upvoteDao: UpvoteDao

    @Inject
    lateinit var favoriteDao: FavoriteDao

    @Inject
    lateinit var httpClient: HttpClient

    @Inject
    lateinit var itemTreeRepository: ItemTreeRepository

    override fun onCreate() {
        super.onCreate()

        coroutineScope.launch {
            itemTreeRepository.cleanupJob()
        }

        // Update upvote and favorite table on login and then periodically.
        coroutineScope.launch {
            authentication.data.distinctUntilChanged().collectLatest { authentication ->
                if (authentication.password?.isNotEmpty() == true) {
                    while (true) {
                        try {
                            val upvoteDef =
                                async {
                                    upvoteDao.replaceUpvotesForUser(
                                        username = authentication.username,
                                        upvotes = getUpvoted(
                                            this@HNApplication,
                                            authentication,
                                            Username(authentication.username)
                                        )
                                            .map { it.long },
                                    )
                                }

                            val favoriteDef =
                                async {
                                    favoriteDao.replaceFavoritesForUser(
                                        username = authentication.username,
                                        favorites = getFavorites(
                                            this@HNApplication,
                                            Username(authentication.username)
                                        )
                                            .map { it.long },
                                    )
                                }

                            delay(
                                TimeUnit.HOURS.toMillis(
                                    resources
                                        .getInteger(com.monoid.hackernews.shared.view.R.integer.favorites_state_hours)
                                        .toLong()
                                )
                            )

                            upvoteDef.await()
                            favoriteDef.await()
                        } catch (error: Throwable) {
                            if (error is CancellationException) throw error
                            error.printStackTrace()
                        }
                    }
                }
            }
        }

        updateAndPushDynamicShortcuts(MainActivity::class.java)

        // register locale changed broadcast receiver
        registerReceiver(
            LocaleChangedBroadcastReceiver(),
            IntentFilter(Intent.ACTION_LOCALE_CHANGED)
        )
    }

    override fun onTerminate() {
        coroutineScope.cancel()
        httpClient.close()
        db.close()
        super.onTerminate()
    }
}
