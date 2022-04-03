package com.monoid.hackernews.navigation

import android.os.Parcelable
import com.monoid.hackernews.api.ItemId
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class LoginAction : Parcelable {
    @Serializable
    @Parcelize
    object Login : LoginAction()

    @Serializable
    @Parcelize
    data class Upvote(val itemId: ItemId) : LoginAction()

    @Serializable
    @Parcelize
    data class Favorite(val itemId: ItemId) : LoginAction()

    @Serializable
    @Parcelize
    data class Reply(val parentId: ItemId, val text: String) : LoginAction()
}
