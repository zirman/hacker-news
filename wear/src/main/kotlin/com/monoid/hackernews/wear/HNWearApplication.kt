package com.monoid.hackernews.wear

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import androidx.room.Room
import com.monoid.hackernews.shared.api.getFavorites
import com.monoid.hackernews.shared.api.getUpvoted
import com.monoid.hackernews.shared.data.Username
import com.monoid.hackernews.shared.data.settingsDataStore
import com.monoid.hackernews.shared.room.FavoriteDao
import com.monoid.hackernews.shared.room.FlagDao
import com.monoid.hackernews.shared.room.HNDatabase
import com.monoid.hackernews.shared.room.UpvoteDao
import com.monoid.hackernews.shared.view.updateAndPushDynamicShortcuts
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

class HNWearApplication : Application() {
    companion object {
        lateinit var instance: HNWearApplication
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
                                            this@HNWearApplication,
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
                                            this@HNWearApplication,
                                            Username(authentication.username)
                                        )
                                            .map { it.long },
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
                                resources
                                    .getInteger(com.monoid.hackernews.shared.view.R.integer.favorites_state_hours)
                                    .toLong()
                            )
                        )
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
        super.onTerminate()
        httpClient.close()
        coroutineScope.cancel()
    }
}
