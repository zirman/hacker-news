package com.monoid.hackernews.common.view.aboutus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.about_us_detail_html
import com.monoid.hackernews.common.view.html.rememberAnnotatedHtmlString
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutUs(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        contentColor = MaterialTheme.colorScheme.tertiary
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    WindowInsets.safeContent
                        .only(
                            run {
                                var windowInsets = WindowInsetsSides.Bottom
                                if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
                                    windowInsets += WindowInsetsSides.Top
                                }
                                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                                    windowInsets += WindowInsetsSides.Horizontal
                                }
                                windowInsets
                            },
                        )
                        .asPaddingValues(),
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val htmlString = stringResource(Res.string.about_us_detail_html)
            Text(
                text = rememberAnnotatedHtmlString(htmlString),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LocalContentColor.current,
                ),
            )
        }
    }
}
