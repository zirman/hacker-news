package com.monoid.hackernews.common.view.settings

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.model.HNFont
import com.monoid.hackernews.common.view.theme.toFontFamily
import com.monoid.hackernews.common.view.theme.toNameId
import org.jetbrains.compose.resources.stringResource

@Composable
fun FontButton(
    font: HNFont,
    selected: Boolean,
    onClickFont: (HNFont) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = {
            onClickFont(font)
        },
        elevation = if (selected) ButtonDefaults.elevatedButtonElevation() else null,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(font.toNameId()),
            fontFamily = font.toFontFamily(),
        )
    }
}
