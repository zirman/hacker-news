package com.monoid.hackernews.common.view.text

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.reply
import org.jetbrains.compose.resources.stringResource

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
        label = { Text(text = stringResource(Res.string.reply)) },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text
        )
    )
}
