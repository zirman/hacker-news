package com.monoid.hackernews.view.main

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.data.Preferences
import com.monoid.hackernews.common.injection.LoggerAdapter
import com.monoid.hackernews.view.theme.HNFont
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    // TODO: repository?
    private val preferencesDataSource: DataStore<Preferences>,
    logger: LoggerAdapter,
) : ViewModel() {
    data class UiState(val selectedFont: HNFont = HNFont.SansSerif)

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    fun setFont(hnFont: HNFont): Job = viewModelScope.launch(context) {
        preferencesDataSource.updateData {
            it.copy(font = hnFont)
        }
    }

    val uiState = preferencesDataSource.data
        .map { UiState(it.font) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
