package com.monoid.hackernews

import android.app.Application
import androidx.room.Room
import com.monoid.hackernews.api.getFavorites
import com.monoid.hackernews.api.getUpvoted
import com.monoid.hackernews.room.FavoriteDao
import com.monoid.hackernews.room.FlagDao
import com.monoid.hackernews.room.HNDatabase
import com.monoid.hackernews.room.UpvoteDao
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
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

    lateinit var upvoteDao: UpvoteDao
        private set

    lateinit var favoriteDao: FavoriteDao
        private set

    lateinit var flagDao: FlagDao
        private set

    lateinit var httpClient: HttpClient
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        coroutineScope =
            CoroutineScope(
                Dispatchers.Main.immediate + CoroutineExceptionHandler { _, error ->
                    error.printStackTrace()
                }
            )

        db = Room
            .databaseBuilder(
                applicationContext,
                HNDatabase::class.java,
                "hacker-news-database"
            )
            .build()

        upvoteDao = db.upvoteDao()
        favoriteDao = db.favoriteDao()
        flagDao = db.flagDao()

        httpClient =
            HttpClient(Android) {
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.ALL
                }

                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }

        // Update upvote and favorite table on login and then periodically.
        coroutineScope.launch {
            settingsDataStore.data.distinctUntilChanged().collectLatest { authentication ->
                if (authentication.password?.isNotEmpty() == true) {
                    while (true) {
                        try {
                            val upvoteDef =
                                async {
                                    upvoteDao.replaceUpvotesForUser(
                                        username = authentication.username,
                                        upvotes = getUpvoted(
                                            authentication,
                                            Username(authentication.username)
                                        ),
                                    )
                                }

                            val favoriteDef =
                                async {
                                    favoriteDao.replaceFavoritesForUser(
                                        username = authentication.username,
                                        favorites = getFavorites(Username(authentication.username)),
                                    )
                                }

                            upvoteDef.await()
                            favoriteDef.await()

                            throw Exception()
                        } catch (error: Throwable) {
                            if (error is CancellationException) throw error
                            error.printStackTrace()
                        }

                        delay(
                            TimeUnit.HOURS.toMillis(
                                resources.getInteger(R.integer.favorites_state_hours).toLong()
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        httpClient.close()
        coroutineScope.cancel()
    }
}
