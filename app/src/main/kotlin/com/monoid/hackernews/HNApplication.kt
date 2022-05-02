package com.monoid.hackernews

import android.app.Application
import androidx.room.Room
import com.monoid.hackernews.api.getFavorites
import com.monoid.hackernews.api.getUpvoted
import com.monoid.hackernews.room.HNDatabase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

class HNApplication : Application() {
    companion object {
        lateinit var instance: HNApplication
            private set
    }

    lateinit var coroutineScope: CoroutineScope
        private set

    lateinit var db: HNDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        coroutineScope =
            CoroutineScope(Dispatchers.Main.immediate + CoroutineExceptionHandler { _, error ->
                error.printStackTrace()
            })

        db = Room
            .databaseBuilder(
                applicationContext,
                HNDatabase::class.java,
                "hacker-news-database"
            )
            .build()

        val upvoteDao = db.upvoteDao()
        val favoriteDao = db.favoriteDao()

        // Update upvote and favorite table on login and then periodically.
        CoroutineScope(Dispatchers.Main.immediate).launch {
            settingsDataStore.data.distinctUntilChanged().collectLatest { authentication ->
                if (authentication.password?.isNotEmpty() == true) {
                    while (true) {
                        try {
                            val upvoteApi =
                                async {
                                    getUpvoted(
                                        authentication,
                                        Username(authentication.username)
                                    )
                                }

                            val favoriteApi =
                                async { getFavorites(Username(authentication.username)) }

                            val upvoteDb = async {
                                upvoteDao.replaceUpvotesForUser(
                                    username = authentication.username,
                                    upvotes = upvoteApi.await(),
                                )
                            }

                            val favoriteDb = async {
                                favoriteDao.replaceFavoritesForUser(
                                    username = authentication.username,
                                    favorites = favoriteApi.await()
                                )
                            }

                            upvoteDb.await()
                            favoriteDb.await()

                            delay(
                                TimeUnit.HOURS.toMillis(
                                    resources.getInteger(R.integer.favorites_state_hours).toLong()
                                ) - Clock.System.now().toEpochMilliseconds()
                            )
                        } catch (error: Throwable) {
                            if (error is CancellationException) throw error
                        }
                    }
                }
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        coroutineScope.cancel()
    }
}
