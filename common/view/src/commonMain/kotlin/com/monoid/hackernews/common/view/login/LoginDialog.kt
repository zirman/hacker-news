package com.monoid.hackernews.common.view.login

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var showErrorText: Int by rememberSaveable {
        mutableIntStateOf(0)
    }
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            loginViewModel.events.collect { event ->
                when (event) {
                    LoginViewModel.Event.DismissRequest -> {
                        onDismissRequest()
                    }

                    LoginViewModel.Event.Error -> {
                        showErrorText = 1
                    }
                }
            }
        }
    }
    val uiState = loginViewModel.uiState.collectAsStateWithLifecycle().value
    @OptIn(ExperimentalMaterial3Api::class)
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center) {
            LoginDialogContent(
                showErrorText = showErrorText != 0,
                loading = uiState.loading,
                onClickSubmit = { username, password ->
                    showErrorText = 0
                    loginViewModel.onSubmit(
                        username = username,
                        password = password,
                    )
                },
                onDismissRequest = onDismissRequest,
            )
        }
    }
}
