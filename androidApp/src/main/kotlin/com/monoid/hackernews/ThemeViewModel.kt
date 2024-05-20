package com.monoid.hackernews

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.data.Preferences
import com.monoid.hackernews.view.theme.HNFont
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(preferencesDataSource: DataStore<Preferences>) : ViewModel() {
    data class UiState(val font: HNFont, val dynamicIfAvailable: Boolean = true)

    val uiState = preferencesDataSource.data
        .map { UiState(font = it.font) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState(HNFont.SansSerif))
}
