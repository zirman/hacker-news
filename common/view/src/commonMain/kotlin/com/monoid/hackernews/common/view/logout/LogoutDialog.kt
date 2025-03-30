package com.monoid.hackernews.common.view.logout

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LogoutDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LogoutViewModel = koinViewModel(),
) {
    @OptIn(ExperimentalMaterial3Api::class)
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center) {
            LogoutDialogContent(
                onClickLogout = {
                    loginViewModel.logout()
                    onDismissRequest()
                },
                onDismissRequest = onDismissRequest,
            )
        }
    }
}
