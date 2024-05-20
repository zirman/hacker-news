package com.monoid.hackernews.view.settings

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.monoid.hackernews.view.theme.HNFont
import com.monoid.hackernews.view.theme.toFontFamily
import com.monoid.hackernews.view.theme.toNameId

@Composable
fun FontButton(
    hnFont: HNFont,
    selected: Boolean,
    onClickFont: (HNFont) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = { onClickFont(hnFont) },
        elevation = if (selected) ButtonDefaults.elevatedButtonElevation() else null,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = hnFont.toNameId()),
            fontFamily = hnFont.toFontFamily(),
        )
    }
}
