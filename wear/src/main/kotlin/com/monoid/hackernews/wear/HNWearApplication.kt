package com.monoid.hackernews.wear

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import androidx.datastore.core.DataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.api.getFavorites
import com.monoid.hackernews.common.api.getUpvoted
import com.monoid.hackernews.common.data.Authentication
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.dataStoreModule
import com.monoid.hackernews.common.databaseModule
import com.monoid.hackernews.common.injection.FirebaseAdapter
import com.monoid.hackernews.common.injection.dispatcherModule
import com.monoid.hackernews.common.injection.firebaseModule
import com.monoid.hackernews.common.networkModule
import com.monoid.hackernews.common.room.FavoriteDao
import com.monoid.hackernews.common.room.HNDatabase
import com.monoid.hackernews.common.room.UpvoteDao
import com.monoid.hackernews.common.view.updateAndPushDynamicShortcuts
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class HNWearApplication : Application() {
    private val firebaseCrashlytics: FirebaseAdapter by inject()
    private val authentication: DataStore<Authentication> by inject()
    private val db: HNDatabase by inject()
    private val upvoteDao: UpvoteDao by inject()
    private val favoriteDao: FavoriteDao by inject()
    private val httpClient: HttpClient by inject()
    private val itemTreeRepository: ItemTreeRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HNWearApplication)
            androidLogger()
            androidFileProperties()
            modules(
                wearApplicationModule,
                dispatcherModule,
                networkModule,
                databaseModule,
                dataStoreModule,
                firebaseModule,
            )
        }

        val lifecycleOwner = ProcessLifecycleOwner.get()

        lifecycleOwner.lifecycleScope.launch(
            CoroutineExceptionHandler { _, error ->
                firebaseCrashlytics.recordException(error)
                error.printStackTrace()
            }
        ) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Update upvote and favorite table on login and then periodically.
                authentication.data.distinctUntilChanged().collectLatest { authentication ->
                    if (authentication.password.isNotEmpty()) {
                        while (true) {
                            try {
                                val upvoteDef = async {
                                    upvoteDao.replaceUpvotesForUser(
                                        username = authentication.username,
                                        upvotes = getUpvoted(
                                            authentication,
                                            Username(authentication.username)
                                        ).map { it.long },
                                    )
                                }

                                val favoriteDef = async {
                                    favoriteDao.replaceFavoritesForUser(
                                        username = authentication.username,
                                        favorites = getFavorites(Username(authentication.username))
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
