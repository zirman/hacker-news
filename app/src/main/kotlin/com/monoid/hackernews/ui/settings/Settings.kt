package com.monoid.hackernews.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.monoid.hackernews.R
import com.monoid.hackernews.ui.theme.HNFont

@Composable
fun Settings(
    windowSizeClassState: State<WindowSizeClass>,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass: WindowSizeClass =
        windowSizeClassState.value

    Surface(
        modifier = modifier,
        contentColor = MaterialTheme.colorScheme.tertiary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
                        WindowInsets.safeContent
                    } else {
                        WindowInsets.safeContent
                            .only(
                                WindowInsetsSides.Start +
                                        WindowInsetsSides.End +
                                        WindowInsetsSides.Bottom
                            )
                    }.asPaddingValues()
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.font))

            FontButton(hnFont = HNFont.SansSerif)
            FontButton(hnFont = HNFont.Serif)
            FontButton(hnFont = HNFont.Monospace)
            FontButton(hnFont = HNFont.Cursive)
        }
    }
}
