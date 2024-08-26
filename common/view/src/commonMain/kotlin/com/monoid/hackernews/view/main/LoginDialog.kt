@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.view.main

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.view.login.LoginDialogContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            loginViewModel.events.collect { event ->
                when (event) {
                    LoginViewModel.Event.DismissRequest -> {
                        onDismissRequest()
                    }
                }
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        LoginDialogContent(
            onClickSubmit = loginViewModel::onSubmit,
        )
    }
}
