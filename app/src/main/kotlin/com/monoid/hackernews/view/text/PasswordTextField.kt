package com.monoid.hackernews.view.text

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monoid.hackernews.shared.view.R

@Composable
fun PasswordTextField(
    password: String,
    onChangePassword: (String) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes labelId: Int = R.string.password,
    @StringRes errorLabelId: Int? = null,
    onNext: (KeyboardActionScope.() -> Unit)? = null,
    onDone: (KeyboardActionScope.() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        val (isPasswordVisible, setPasswordVisible) =
            rememberSaveable { mutableStateOf(false) }

        OutlinedTextField(
            value = password,
            onValueChange = onChangePassword,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = labelId)) },
            placeholder = { Text(text = stringResource(id = R.string.password)) },
            trailingIcon = {
                IconToggleButton(
                    checked = isPasswordVisible,
                    onCheckedChange = { setPasswordVisible(it) },
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = stringResource(id = R.string.password_toggle),
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = errorLabelId != null,
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Password,
                imeAction = if (onNext != null) ImeAction.Next else ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onNext = onNext,
                onDone = onDone,
            ),
            singleLine = true,
        )

        if (errorLabelId != null) {
            Text(
                text = stringResource(id = errorLabelId),
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
    }
}
