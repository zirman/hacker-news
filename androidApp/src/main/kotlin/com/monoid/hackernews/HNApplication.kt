package com.monoid.hackernews

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import androidx.datastore.core.DataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.api.getFavorites
import com.monoid.hackernews.common.api.getUpvoted
import com.monoid.hackernews.common.data.Preferences
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.dataStoreModule
import com.monoid.hackernews.common.databaseModule
import com.monoid.hackernews.common.injection.LoggerAdapter
import com.monoid.hackernews.common.injection.dispatcherModule
import com.monoid.hackernews.common.injection.loggerModule
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
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import java.util.concurrent.TimeUnit

class HNApplication : Application() {
    private val logger: LoggerAdapter by inject()
    private val preferences: DataStore<Preferences> by inject()
    private val upvoteLocalDataSource: UpvoteDao by inject()
    private val favoriteLocalDataSource: FavoriteDao by inject()
    private val remoteDataSource: HttpClient by inject()
    private val database: HNDatabase by inject()
    private val applicationLifecycleOwner: LifecycleOwner by inject(named(LifecycleOwnerQualifier.ApplicationLifecycleOwner))

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HNApplication)
            androidLogger()
            androidFileProperties()
            modules(
                applicationModule,
                dispatcherModule,
                networkModule,
                databaseModule,
                dataStoreModule,
                loggerModule,
            )
        }

        applicationLifecycleOwner.lifecycleScope.launch(context) {
            applicationLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Update upvote and favorite table on login and then periodically.
                preferences.data.distinctUntilChanged().collectLatest { authentication ->
                    if (authentication.password.string.isNotEmpty()) {
                        while (true) {
                            try {
                                val upvoteDef =
                                    async {
                                        upvoteLocalDataSource.replaceUpvotesForUser(
                                            username = authentication.username.string,
                                            upvotes = getUpvoted(
                                                preferences = authentication,
                                                username = Username(authentication.username.string),
                                            ).map { it.long },
                                        )
                                    }

                                val favoriteDef =
                                    async {
                                        favoriteLocalDataSource.replaceFavoritesForUser(
                                            username = authentication.username.string,
                                            favorites = getFavorites(
                                                username = Username(authentication.username.string),
                                            ).map { it.long },
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
                            } catch (throwable: Throwable) {
                                currentCoroutineContext().ensureActive()
                                logger.recordException(
                                    messageString = "OnCreate",
                                    throwable = throwable,
                                    tag = TAG,
                                )
                            }
                        }
                    }
                }
            }
        }

        updateAndPushDynamicShortcuts(MainActivity::class.java)

        // register locale changed broadcast receiver
        registerReceiver(
            /* receiver = */ LocaleChangedBroadcastReceiver(),
            /* filter = */ IntentFilter(Intent.ACTION_LOCALE_CHANGED),
        )
    }

    override fun onTerminate() {
        remoteDataSource.close()
        database.close()
        super.onTerminate()
    }

    companion object {
        private const val TAG = "HNApplication"
    }
}
