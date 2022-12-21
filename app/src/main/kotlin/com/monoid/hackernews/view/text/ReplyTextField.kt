package com.monoid.hackernews.view.text

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.monoid.hackernews.common.view.R

@Composable
fun ReplyTextField(
    reply: String,
    onReplyChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = reply,
        onValueChange = onReplyChange,
        modifier = modifier,
        label = { Text(text = stringResource(id = R.string.reply)) },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
        ),
    )
}
