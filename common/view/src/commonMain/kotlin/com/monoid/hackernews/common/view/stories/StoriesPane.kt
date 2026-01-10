package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.an_error_occurred_format
import com.monoid.hackernews.common.view.fab.listContentInsetSides
import io.ktor.http.Url
import org.jetbrains.compose.resources.getString

@Composable
fun StoriesPane(
    onClickLogin: () -> Unit,
    onClickUser: (Username) -> Unit,
    onClickStory: (Item) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: StoriesViewModel = metroViewModel(
        extras = StoriesViewModel.extras(StoryOrdering.Trending),
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    LocalPlatformContext.current
    val platformContext = LocalPlatformContext.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            for (event in viewModel.events) {
                when (event) {
                    is StoriesViewModel.Event.Error -> {
                        platformContext.displayMessage(
                            getString(
                                Res.string.an_error_occurred_format,
                                event.message.orEmpty(),
                            ),
                        )
                    }

                    is StoriesViewModel.Event.OpenLogin -> {
                        onClickLogin()
                    }
                }
            }
        }
    }
    StoriesListPane(
        onClickItem = onClickStory,
        onClickReply = onClickReply,
        onClickUser = onClickUser,
        onClickUrl = onClickUrl,
        onClickLogin = onClickLogin,
        contentPadding = WindowInsets.safeDrawing
            .only(listContentInsetSides())
            .asPaddingValues(),
        modifier = modifier,
    )
}
