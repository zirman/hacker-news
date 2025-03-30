package com.monoid.hackernews.common.data.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.monoid.hackernews.common.core.LoggerAdapter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Single

@Single
class SettingsRepository(
    private val logger: LoggerAdapter,
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

    val isLoggedIn: Boolean get() = preferences.value.username.string.isNotEmpty()
}

private const val TAG = "StoriesRepository"
