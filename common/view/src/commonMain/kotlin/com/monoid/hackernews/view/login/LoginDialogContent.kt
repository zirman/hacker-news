package com.monoid.hackernews.view.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.model.Password
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.hacker_news_login
import com.monoid.hackernews.common.view.i_agree_html
import com.monoid.hackernews.common.view.rememberAnnotatedHtmlString
import com.monoid.hackernews.common.view.submit
import com.monoid.hackernews.view.text.PasswordTextField
import com.monoid.hackernews.view.text.UsernameTextField
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginDialogContent(
    onClickSubmit: (Username, Password) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            var username by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }

            Text(
                text = stringResource(Res.string.hacker_news_login),
                style = MaterialTheme.typography.headlineMedium,
            )

            Column {
                val focusManager: FocusManager =
                    LocalFocusManager.current

                UsernameTextField(
                    username = username,
                    onUsernameChange = { username = it },
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    onPrev = { focusManager.moveFocus(FocusDirection.Up) },
                )

                PasswordTextField(
                    password = password,
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
            val acceptTermsState = rememberSaveable {
                mutableStateOf(false)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = acceptTermsState.value,
                    onCheckedChange = {
                        acceptTermsState.value = acceptTermsState.value.not()
                    },
                )
                val htmlString = stringResource(Res.string.i_agree_html)
                Text(
                    text = rememberAnnotatedHtmlString(htmlString),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LocalContentColor.current,
                    ),
                )
            }

            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotEmpty()) {
                        onClickSubmit(Username(username), Password(password))
                    }
                },
                enabled = username.isNotBlank() &&
                    password.isNotEmpty() &&
                    acceptTermsState.value,
            ) {
                Text(text = stringResource(Res.string.submit))
            }
        }
    }
}
