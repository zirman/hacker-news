// package com.monoid.hackernews.common.view.reply
//
// import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.WindowInsets
// import androidx.compose.foundation.layout.WindowInsetsSides
// import androidx.compose.foundation.layout.asPaddingValues
// import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.only
// import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.layout.safeContent
// import androidx.compose.foundation.layout.width
// import androidx.compose.foundation.text.selection.SelectionContainer
// import androidx.compose.material3.Button
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Surface
// import androidx.compose.material3.Text
// import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
// import androidx.compose.material3.windowsizeclass.WindowSizeClass
// import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
// import androidx.compose.runtime.Composable
// import androidx.compose.runtime.getValue
// import androidx.compose.runtime.mutableStateOf
// import androidx.compose.runtime.remember
// import androidx.compose.runtime.rememberCoroutineScope
// import androidx.compose.runtime.saveable.rememberSaveable
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.res.stringResource
// import androidx.compose.ui.text.AnnotatedString
// import androidx.compose.ui.text.fromHtml
// import androidx.compose.ui.unit.dp
// import androidx.datastore.core.DataStore
// import androidx.compose.runtime.collectAsState
// import com.monoid.hackernews.common.api.ItemId
// import com.monoid.hackernews.common.api.commentRequest
// import com.monoid.hackernews.common.data.ItemUi
// import com.monoid.hackernews.common.data.Preferences
// import com.monoid.hackernews.common.view.R
// import com.monoid.hackernews.view.text.ReplyTextField
// import io.ktor.client.HttpClient
// import kotlinx.coroutines.CoroutineScope
// import kotlinx.coroutines.Job
// import kotlinx.coroutines.currentCoroutineContext
// import kotlinx.coroutines.ensureActive
// import kotlinx.coroutines.flow.first
// import kotlinx.coroutines.launch
//
// @Composable
// fun ReplyContent(
//     httpClient: HttpClient,
//     itemTreeRepository: ItemTreeRepository,
//     preferences: DataStore<Preferences>,
//     itemId: ItemId,
//     windowSizeClass: WindowSizeClass,
//     onSuccess: () -> Unit,
//     onError: (Throwable) -> Unit,
//     modifier: Modifier = Modifier,
// ) {
//     val coroutineScope = rememberCoroutineScope()
//
//     val itemUi: ItemUi? by remember {
//         itemTreeRepository.itemUiList(listOf(itemId)).first().itemUiFlow(coroutineScope)
//     }.collectAsStateWithLifecycle()
//
//     val item = itemUi?.item
//
//     Surface(
//         modifier = modifier,
//         contentColor = MaterialTheme.colorScheme.tertiary,
//     ) {
//         Column(
//             modifier = Modifier
//                 .fillMaxWidth()
//                 .padding(
//                     WindowInsets.safeContent
//                         .only(run {
//                             var windowInsets = WindowInsetsSides.Bottom
//                             if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
//                                 windowInsets += WindowInsetsSides.Top
//                             }
//                             if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
//                                 windowInsets += WindowInsetsSides.Horizontal
//                             }
//                             windowInsets
//                         })
//                         .asPaddingValues()
//                 ),
//             horizontalAlignment = Alignment.CenterHorizontally,
//         ) {
//             val rowModifier: Modifier =
//                 if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
//                     Modifier
//                         .fillMaxWidth()
//                         .padding(horizontal = 16.dp)
//                 } else {
//                     Modifier.width(320.dp)
//                 }.padding(vertical = 4.dp)
//
//             val (reply: String, setReply) =
//                 rememberSaveable(item?.text) {
//                     mutableStateOf(
//                         AnnotatedString.fromHtml(htmlString = item?.text ?: "")
//                             .text
//                             .lines()
//                             .filter { it.isNotBlank() }
//                             .mapIndexed { index, s ->
//                                 if (index == 0) {
//                                     "> $s\n"
//                                 } else {
//                                     "\n> $s\n"
//                                 }
//                             }
//                             .joinToString("")
//                     )
//                 }
//
//             val title = item?.title
//             if (title != null) {
//                 SelectionContainer {
//                     Text(
//                         text = title,
//                         modifier = rowModifier.padding(top = 8.dp),
//                         style = MaterialTheme.typography.bodyLarge,
//                     )
//                 }
//             }
//
//             ReplyTextField(
//                 reply = reply,
//                 onReplyChange = setReply,
//                 modifier = rowModifier.padding(top = 8.dp),
//             )
//
//             Button(
//                 onClick = {
//                     replyJob(
//                         preferences = preferences,
//                         coroutineScope = coroutineScope,
//                         httpClient = httpClient,
//                         itemId = itemId,
//                         text = reply,
//                         onSuccess = onSuccess,
//                         onError = onError,
//                     )
//                 },
//                 modifier = rowModifier,
//                 enabled = reply.isNotBlank(),
//             ) {
//                 Text(text = stringResource(id = R.string.send))
//             }
//         }
//     }
// }
//
// fun replyJob(
//     preferences: DataStore<Preferences>,
//     coroutineScope: CoroutineScope,
//     httpClient: HttpClient,
//     itemId: ItemId,
//     text: String,
//     onSuccess: () -> Unit,
//     onError: (Throwable) -> Unit,
// ): Job {
//     return coroutineScope.launch {
//         try {
//             httpClient.commentRequest(
//                 preferences = preferences.data.first(),
//                 parentId = itemId,
//                 text = text,
//             )
//
//             onSuccess()
//         } catch (throwable: Throwable) {
//             currentCoroutineContext().ensureActive()
//             onError(throwable)
//         }
//     }
// }
