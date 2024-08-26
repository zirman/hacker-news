package com.monoid.hackernews.view.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SettingsDetailUiState : Parcelable {
    Profile,
    Styling,
}
