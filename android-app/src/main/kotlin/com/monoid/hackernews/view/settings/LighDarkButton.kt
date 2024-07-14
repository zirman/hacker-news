package com.monoid.hackernews.view.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.monoid.hackernews.common.data.LightDarkMode
import com.monoid.hackernews.view.theme.toNameId

@Composable
fun LightDarkButton(
    lightDarkMode: LightDarkMode,
    selected: Boolean,
    onClickLightDarkMode: (LightDarkMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = { onClickLightDarkMode(lightDarkMode) },
        )
        Text(text = stringResource(id = lightDarkMode.toNameId()))
    }
}
