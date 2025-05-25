package com.monoid.hackernews.common.view.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.monoid.hackernews.common.view.settings.AppearanceViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun appColorScheme(): ColorScheme

@Composable
expect fun AppTheme(
    viewModel: AppearanceViewModel = koinViewModel(),
    content: @Composable () -> Unit,
)
