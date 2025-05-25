package com.monoid.hackernews.common.view.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.model.Password
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.an_error_occurred
import com.monoid.hackernews.common.view.cancel
import com.monoid.hackernews.common.view.hacker_news_login
import com.monoid.hackernews.common.view.html.rememberAnnotatedHtmlString
import com.monoid.hackernews.common.view.i_agree_html
import com.monoid.hackernews.common.view.itemdetail.htmlTextStyle
import com.monoid.hackernews.common.view.platform.PlatformLoadingIndicator
import com.monoid.hackernews.common.view.submit
import com.monoid.hackernews.common.view.text.PasswordTextField
import com.monoid.hackernews.common.view.text.UsernameTextField
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginDialogContent(
    showErrorText: Boolean,
    loading: Boolean,
    onClickSubmit: (Username, Password) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            var username by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            Text(
                text = stringResource(Res.string.hacker_news_login),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Column {
                val focusManager: FocusManager = LocalFocusManager.current
                UsernameTextField(
                    username = username,
                    enabled = loading.not(),
                    onUsernameChange = { username = it },
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    onPrev = { focusManager.moveFocus(FocusDirection.Up) },
                )
                PasswordTextField(
                    password = password,
                    enabled = loading.not(),
                    onChangePassword = { password = it },
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    onPrev = { focusManager.moveFocus(FocusDirection.Up) },
                    onDone = {
                        if (username.isNotBlank() && password.isNotEmpty()) {
                            onClickSubmit(Username(username), Password(password))
                        }
                    },
                )
            }
            var acceptTermsState by rememberSaveable {
                mutableStateOf(false)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = acceptTermsState,
                    enabled = loading.not(),
                    onCheckedChange = {
                        acceptTermsState = it
                    },
                )
                val htmlString = stringResource(Res.string.i_agree_html)
                Text(
                    text = rememberAnnotatedHtmlString(htmlString),
                    style = htmlTextStyle().merge(MaterialTheme.typography.bodyMedium),
                )
            }
            if (showErrorText) {
                Text(
                    text = stringResource(Res.string.an_error_occurred),
                    style = LocalTextStyle.current.merge(MaterialTheme.typography.bodyLarge),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(Res.string.cancel))
                }
                TextButton(
                    onClick = {
                        if (username.isNotBlank() && password.isNotEmpty()) {
                            onClickSubmit(Username(username), Password(password))
                        }
                    },
                    enabled = loading.not() &&
                        username.isNotBlank() &&
                        password.isNotEmpty() &&
                        acceptTermsState,
                ) {
                    Text(text = stringResource(Res.string.submit))
                    AnimatedVisibility(loading) {
                        PlatformLoadingIndicator(
                            modifier = Modifier.padding(start = 16.dp).size(24.dp),
                        )
                    }
                }
            }
        }
    }
}
