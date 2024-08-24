package com.monoid.hackernews.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.monoid.hackernews.common.api.loginRequest
import com.monoid.hackernews.common.injection.LoggerAdapter
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
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

    val preferences = localDataSource.data
        .map { it.settings ?: Settings() }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = Settings(),
        )

    suspend fun login(username: Username, password: Password) {
        remoteDataSource.loginRequest(
            settings = Settings(
                username = username,
                password = password,
            )
        )
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = (settings ?: Settings()).copy(username = username, password = password)
            }
        }
    }

    suspend fun logout() {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings =
                    (settings ?: Settings()).copy(username = Username(""), password = Password(""))
            }
        }
    }

    companion object {
        private const val TAG = "StoriesRepository"
    }
}
