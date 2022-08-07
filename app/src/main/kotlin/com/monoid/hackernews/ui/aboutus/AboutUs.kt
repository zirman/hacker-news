package com.monoid.hackernews.ui.aboutus

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.R
import com.monoid.hackernews.util.onClick
import com.monoid.hackernews.util.rememberAnnotatedString

@Composable
fun AboutUs(
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
            val annotatedTextState: State<AnnotatedString> =
                rememberUpdatedState(
                    rememberAnnotatedString(
                        text = stringResource(id = R.string.about_us_detail),
                        linkColor = MaterialTheme.colorScheme.primary,
                    )
                )

            val contextState: State<Context> =
                rememberUpdatedState(LocalContext.current)

            ClickableText(
                text = annotatedTextState.value,
                onClick = { offset ->
                    annotatedTextState.value.onClick(
                        context = contextState.value,
                        offset = offset,
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LocalContentColor.current,
                ),
            )
        }
    }
}