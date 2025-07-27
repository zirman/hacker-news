@file:OptIn(ExperimentalLayoutApi::class)

package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.model.HNFont
import com.monoid.hackernews.common.data.model.LightDarkMode
import com.monoid.hackernews.common.data.model.Shape
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.font_size_format
import com.monoid.hackernews.common.view.fonts
import com.monoid.hackernews.common.view.light_dark_mode
import com.monoid.hackernews.common.view.line_spacing_format
import com.monoid.hackernews.common.view.paragraph_indent_format
import com.monoid.hackernews.common.view.shapes
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppearanceDetailPane(
    modifier: Modifier = Modifier,
    viewModel: AppearanceViewModel = koinViewModel(),
) {
    Surface(
        modifier = modifier
            .fillMaxSize(),
    ) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val selectedLightDarkMode = uiState.lightDarkMode
        val selectedFont = uiState.font
        val fontSize = uiState.fontSize
        val lineSpacing = uiState.lineHeight
        val paragraphIndent = uiState.paragraphIndent
        val selectedShape = uiState.shape
        Column(
            modifier = Modifier
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .padding(16.dp),
        ) {
            Text(stringResource(Res.string.light_dark_mode))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LightDarkMode.entries.forEach { lightDarkMode ->
                    LightDarkButton(
                        lightDarkMode = lightDarkMode,
                        selected = lightDarkMode == selectedLightDarkMode,
                        onClickLightDarkMode = viewModel::onClickLightDarkMode,
                    )
                }
            }
            Text(stringResource(Res.string.fonts))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                HNFont.entries.forEach { font ->
                    FontButton(
                        font = font,
                        selected = font == selectedFont,
                        onClickFont = viewModel::onClickFont,
                    )
                }
            }
            Text(stringResource(Res.string.font_size_format, fontSize.delta))
            FlowRow {
                IconButton(onClick = viewModel::onClickIncreaseFontSize) {
                    Icon(
                        imageVector = Icons.TwoTone.Add,
                        contentDescription = null,
                    )
                }
                IconButton(onClick = viewModel::onClickDecreaseFontSize) {
                    Icon(
                        imageVector = Icons.TwoTone.Remove,
                        contentDescription = null,
                    )
                }
            }
            Text(stringResource(Res.string.line_spacing_format, lineSpacing.delta))
            FlowRow {
                IconButton(onClick = viewModel::onClickIncreaseLineHeight) {
                    Icon(
                        imageVector = Icons.TwoTone.Add,
                        contentDescription = null,
                    )
                }
                IconButton(onClick = viewModel::onClickDecreaseLineHeight) {
                    Icon(
                        imageVector = Icons.TwoTone.Remove,
                        contentDescription = null,
                    )
                }
            }
            Text(stringResource(Res.string.paragraph_indent_format, paragraphIndent.em))
            FlowRow {
                IconButton(onClick = viewModel::onClickIncreaseParagraphIndent) {
                    Icon(
                        imageVector = Icons.TwoTone.Add,
                        contentDescription = null,
                    )
                }
                IconButton(onClick = viewModel::onClickDecreaseParagraphIndent) {
                    Icon(
                        imageVector = Icons.TwoTone.Remove,
                        contentDescription = null,
                    )
                }
            }
            Text(stringResource(Res.string.shapes))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Shape.entries.forEach { shape ->
                    ShapeButton(
                        shape = shape,
                        selected = shape == selectedShape,
                        onClickShape = viewModel::onClickShape,
                    )
                }
            }
            // TODO: colors
        }
    }
}
