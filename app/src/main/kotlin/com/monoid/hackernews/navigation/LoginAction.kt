package com.monoid.hackernews.navigation

import android.os.Parcelable
import com.monoid.hackernews.api.ItemId
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable

@Serializable
sealed class LoginAction : Parcelable {
    @Serializable
    @Parcelize
    object Login : LoginAction()

    @Serializable
    @Parcelize
    data class Upvote(val itemId: Long) : LoginAction()

    @Serializable
    @Parcelize
    data class Favorite(val itemId: Long) : LoginAction()

    @Serializable
    @Parcelize
    data class Reply(val parentId: Long, val text: String) : LoginAction()
}
