package com.monoid.hackernews.common.data

import androidx.datastore.core.DataStore
import com.monoid.hackernews.common.injection.LoggerAdapter
import com.monoid.hackernews.view.theme.HNFont
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PreferencesRepository(
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

    val preferences = localDataSource.data.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = Preferences(),
    )

    suspend fun setLightDarkMode(lightDarkMode: LightDarkMode) {
        localDataSource.updateData { preferences ->
            preferences.copy(lightDarkMode = lightDarkMode)
        }
    }

    suspend fun setFont(font: HNFont) {
        localDataSource.updateData { preferences ->
            preferences.copy(font = font)
        }
    }

    suspend fun increaseFontSize() {
        localDataSource.updateData { preferences ->
            preferences.copy(fontSize = preferences.fontSize.increaseSize())
        }
    }

    suspend fun decreaseFontSize() {
        localDataSource.updateData { preferences ->
            preferences.copy(fontSize = preferences.fontSize.decreaseSize())
        }
    }

    suspend fun increaseLineHeight() {
        localDataSource.updateData { preferences ->
            preferences.copy(lineHeight = preferences.lineHeight.increaseSize())
        }
    }

    suspend fun decreaseLineHeight() {
        localDataSource.updateData { preferences ->
            preferences.copy(lineHeight = preferences.lineHeight.decreaseSize())
        }
    }

    suspend fun increaseParagraphIndent() {
        localDataSource.updateData { preferences ->
            preferences.copy(paragraphIndent = preferences.paragraphIndent.increaseSize())
        }
    }

    suspend fun decreaseParagraphIndent() {
        localDataSource.updateData { preferences ->
            preferences.copy(paragraphIndent = preferences.paragraphIndent.decreaseSize())
        }
    }

    suspend fun setShape(shape: Shape) {
        localDataSource.updateData { preferences ->
            preferences.copy(shape = shape)
        }
    }

    companion object {
        private const val TAG = "StoriesRepository"
    }
}
