package com.monoid.hackernews.view.settings

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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.view.theme.HNFont

@Composable
fun Settings(
    authentication: DataStore<Authentication>,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        contentColor = MaterialTheme.colorScheme.tertiary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    WindowInsets.safeContent
                        .only(run {
                            var windowInsets = WindowInsetsSides.Bottom
                            if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
                                windowInsets += WindowInsetsSides.Top
                            }
                            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                                windowInsets += WindowInsetsSides.Horizontal
                            }
                            windowInsets
                        })
                        .asPaddingValues()
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.font))

            FontButton(
                hnFont = HNFont.SansSerif,
                authentication = authentication,
            )

            FontButton(
                hnFont = HNFont.Serif,
                authentication = authentication,
            )

            FontButton(
                hnFont = HNFont.Monospace,
                authentication = authentication,
            )

            FontButton(
                hnFont = HNFont.Cursive,
                authentication = authentication,
            )
        }
    }
}
