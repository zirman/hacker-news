package com.monoid.hackernews.common.data

import androidx.datastore.core.DataStore
import com.monoid.hackernews.common.api.loginRequest
import com.monoid.hackernews.common.injection.LoggerAdapter
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class LoginRepository(
    private val logger: LoggerAdapter,
    private val remoteDataSource: HttpClient,
    private val localDataSource: DataStore<Preferences>,
) {
    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    private val scope = CoroutineScope(context)

    val preferences = localDataSource.data.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = Preferences(),
    )

    suspend fun login(username: Username, password: Password) {
        localDataSource.updateData { auth ->
            remoteDataSource.loginRequest(
                preferences = Preferences(
                    username = username,
                    password = password,
                )
            )

            auth.copy(
                username = username,
                password = password,
            )
        }
    }

    suspend fun logout() {
        localDataSource.updateData { preferences ->
            preferences.copy(username = Username(""), password = Password(""))
        }
    }

    companion object {
        private const val TAG = "StoriesRepository"
    }
}
