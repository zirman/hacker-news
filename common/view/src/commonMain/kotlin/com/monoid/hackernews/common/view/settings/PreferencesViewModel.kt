package com.monoid.hackernews.common.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.data.model.Colors
import com.monoid.hackernews.common.data.model.FontSize
import com.monoid.hackernews.common.data.model.HNFont
import com.monoid.hackernews.common.data.model.LightDarkMode
import com.monoid.hackernews.common.data.model.LineHeight
import com.monoid.hackernews.common.data.model.ParagraphIndent
import com.monoid.hackernews.common.data.model.Settings
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.model.Shape
import com.monoid.hackernews.common.core.LoggerAdapter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PreferencesViewModel(
    private val repository: SettingsRepository,
    logger: LoggerAdapter,
) : ViewModel() {
    data class UiState(
        val lightDarkMode: LightDarkMode = LightDarkMode.default,
        val font: HNFont = HNFont.default,
        val fontSize: FontSize = FontSize.default,
        val lineHeight: LineHeight = LineHeight.default,
        val paragraphIndent: ParagraphIndent = ParagraphIndent.default,
        val shape: Shape = Shape.default,
        val colors: Colors = Colors.default,
    )

    val uiState = repository.preferences
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = repository.preferences.value.toUiState(),
        )

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    fun onClickLightDarkMode(lightDarkMode: LightDarkMode): Job = viewModelScope.launch(context) {
        repository.setLightDarkMode(lightDarkMode)
    }

    fun onClickFont(font: HNFont): Job = viewModelScope.launch(context) {
        repository.setFont(font)
    }

    fun onClickIncreaseFontSize(): Job = viewModelScope.launch(context) {
        repository.increaseFontSize()
    }

    fun onClickDecreaseFontSize(): Job = viewModelScope.launch(context) {
        repository.decreaseFontSize()
    }

    fun onClickIncreaseLineHeight(): Job = viewModelScope.launch(context) {
        repository.increaseLineHeight()
    }

    fun onClickDecreaseLineHeight(): Job = viewModelScope.launch(context) {
        repository.decreaseLineHeight()
    }

    fun onClickIncreaseParagraphIndent(): Job = viewModelScope.launch(context) {
        repository.increaseParagraphIndent()
    }

    fun onClickDecreaseParagraphIndent(): Job = viewModelScope.launch(context) {
        repository.decreaseParagraphIndent()
    }

    fun onClickShape(shape: Shape): Job = viewModelScope.launch(context) {
        repository.setShape(shape)
    }

    companion object {
        private const val TAG = "PreferencesViewModel"

        private fun Settings.toUiState(): UiState = UiState(
            lightDarkMode = lightDarkMode,
            font = font,
            fontSize = fontSize,
            lineHeight = lineHeight,
            paragraphIndent = paragraphIndent,
            shape = shape,
            colors = colors,
        )
    }
}
