package com.monoid.hackernews.common.view.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.monoid.hackernews.common.view.metroViewModel
import com.monoid.hackernews.common.view.settings.AppearanceViewModel

@Composable
expect fun appColorScheme(): ColorScheme

@Composable
expect fun AppTheme(
    viewModel: AppearanceViewModel = metroViewModel(),
    content: @Composable () -> Unit,
)
