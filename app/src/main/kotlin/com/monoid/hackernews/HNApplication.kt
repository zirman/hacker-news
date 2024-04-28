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
import com.monoid.hackernews.common.dataStoreModule
import com.monoid.hackernews.common.databaseModule
import com.monoid.hackernews.common.datastore.Authentication
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
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import java.util.concurrent.TimeUnit

class HNApplication : Application() {
    private val firebaseCrashlytics: FirebaseCrashlytics by inject()
    private val authentication: DataStore<Authentication> by inject()
    private val db: HNDatabase by inject()
    private val upvoteDao: UpvoteDao by inject()
    private val favoriteDao: FavoriteDao by inject()
    private val httpClient: HttpClient by inject()
    private val itemTreeRepository: ItemTreeRepository by inject()
    private val applicationLifecycleOwner: LifecycleOwner by inject(named(LifecycleOwnerQualifier.ApplicationLifecycleOwner))

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
                firebaseModule,
            )
        }

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
                                                context = this@HNApplication,
                                                authentication = authentication,
                                                username = Username(authentication.username),
                                            ).map { it.long },
                                        )
                                    }

                                val favoriteDef =
                                    async {
                                        favoriteDao.replaceFavoritesForUser(
                                            username = authentication.username,
                                            favorites = getFavorites(
                                                context = this@HNApplication,
                                                username = Username(authentication.username),
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
            /* receiver = */ LocaleChangedBroadcastReceiver(),
            /* filter = */ IntentFilter(Intent.ACTION_LOCALE_CHANGED),
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
