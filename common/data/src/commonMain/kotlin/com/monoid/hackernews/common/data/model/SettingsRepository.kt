package com.monoid.hackernews.common.data.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.data.api.loginRequest
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@SingleIn(AppScope::class)
@Inject
class SettingsRepository(
    private val logger: LoggerAdapter,
    private val remoteDataSource: HttpClient,
    private val localDataSource: DataStore<Preferences>,
) {
    private val scope = CoroutineScope(
        CoroutineExceptionHandler { _, throwable ->
            logger.recordException(
                messageString = "CoroutineExceptionHandler",
                throwable = throwable,
                tag = TAG,
            )
        },
    )

    val preferences = localDataSource.data
        .map { it.settings ?: Settings() }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = Settings(),
        )

    suspend fun setLightDarkMode(lightDarkMode: LightDarkMode) {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = (settings ?: Settings()).copy(lightDarkMode = lightDarkMode)
            }
        }
    }

    suspend fun setFont(font: HNFont) {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = (settings ?: Settings()).copy(font = font)
            }
        }
    }

    suspend fun increaseFontSize() {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = (settings ?: Settings()).run { copy(fontSize = fontSize.increaseSize()) }
            }
        }
    }

    suspend fun decreaseFontSize() {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = (settings ?: Settings()).run { copy(fontSize = fontSize.decreaseSize()) }
            }
        }
    }

    suspend fun increaseLineHeight() {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings =
                    (settings ?: Settings()).run { copy(lineHeight = lineHeight.increaseSize()) }
            }
        }
    }

    suspend fun decreaseLineHeight() {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = run { settings ?: Settings() }
                    .run { copy(lineHeight = lineHeight.decreaseSize()) }
            }
        }
    }

    suspend fun increaseParagraphIndent() {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = run { settings ?: Settings() }
                    .run { copy(paragraphIndent = paragraphIndent.increaseSize()) }
            }
        }
    }

    suspend fun decreaseParagraphIndent() {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = run { settings ?: Settings() }
                    .run { copy(paragraphIndent = paragraphIndent.decreaseSize()) }
            }
        }
    }

    suspend fun setShape(shape: Shape) {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = run { settings ?: Settings() }
                    .copy(shape = shape)
            }
        }
    }

    suspend fun setNotifications(enabled: Boolean) {
        localDataSource.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                settings = run { settings ?: Settings() }
                    .copy(notifications = enabled)
            }
        }
    }

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

    val isLoggedIn: Boolean get() = preferences.value.username.string.isNotEmpty()
}

private const val TAG = "StoriesRepository"
