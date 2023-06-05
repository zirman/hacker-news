package com.monoid.hackernews

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import androidx.datastore.core.DataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.monoid.hackernews.common.api.getFavorites
import com.monoid.hackernews.common.api.getUpvoted
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.room.FavoriteDao
import com.monoid.hackernews.common.room.HNDatabase
import com.monoid.hackernews.common.room.UpvoteDao
import com.monoid.hackernews.common.view.updateAndPushDynamicShortcuts
import dagger.hilt.android.HiltAndroidApp
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class HNApplication : Application() {

    @Inject
    lateinit var mainCoroutineDispatcher: MainCoroutineDispatcher

    @Inject
    lateinit var firebaseCrashlytics: FirebaseCrashlytics

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

    @ApplicationLifecycleOwner
    @Inject
    lateinit var applicationLifecycleOwner: LifecycleOwner

    override fun onCreate() {
        super.onCreate()

        applicationLifecycleOwner.lifecycleScope.launch(
            CoroutineExceptionHandler { _, error ->
                firebaseCrashlytics.recordException(error)
                error.printStackTrace()
            }
        ) {
            applicationLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Update upvote and favorite table on login and then periodically.
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
                                            .getInteger(com.monoid.hackernews.common.view.R.integer.favorites_state_hours)
                                            .toLong()
                                    )
                                )

                                upvoteDef.await()
                                favoriteDef.await()
                            } catch (error: Throwable) {
                                currentCoroutineContext().ensureActive()
                                error.printStackTrace()
                            }
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

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        itemTreeRepository.cleanup()
    }

    override fun onTerminate() {
        httpClient.close()
        db.close()
        super.onTerminate()
    }
}
