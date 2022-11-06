package com.monoid.hackernews.shared.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
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
    data class Flag(val itemId: Long) : LoginAction()

    @Serializable
    @Parcelize
    data class Reply(val itemId: Long) : LoginAction()
}
