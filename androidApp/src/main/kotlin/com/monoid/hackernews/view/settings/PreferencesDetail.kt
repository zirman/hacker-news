@file:OptIn(ExperimentalLayoutApi::class)

package com.monoid.hackernews.view.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.LightDarkMode
import com.monoid.hackernews.common.data.Shape
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.view.theme.HNFont
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreferencesDetail(
    modifier: Modifier = Modifier,
    viewModel: PreferencesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (selectedLightDarkMode, selectedFont, fontSize, lineSpacing, paragraphIndent, selectedShape, colors) = uiState
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .padding(WindowInsets.safeDrawing.asPaddingValues()),
    ) {
        Text(stringResource(R.string.light_dark_mode))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LightDarkMode.entries.forEach { lightDarkMode ->
                LightDarkButton(
                    lightDarkMode = lightDarkMode,
                    selected = lightDarkMode == selectedLightDarkMode,
                    onClickLightDarkMode = viewModel::onClickLightDarkMode,
                )
            }
        }
        Text(stringResource(R.string.fonts))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            HNFont.entries.forEach { font ->
                FontButton(
                    font = font,
                    selected = font == selectedFont,
                    onClickFont = viewModel::onClickFont,
                )
            }
        }
        Text(stringResource(R.string.font_size_format, fontSize.delta))
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
        Text(stringResource(R.string.line_spacing_format, lineSpacing.delta))
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
        Text(stringResource(R.string.paragraph_indent_format, paragraphIndent.em))
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
        Text(stringResource(R.string.shapes))
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
