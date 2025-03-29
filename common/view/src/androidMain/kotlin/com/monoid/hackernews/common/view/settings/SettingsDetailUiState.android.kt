package com.monoid.hackernews.common.view.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SettingsDetailUiState : Parcelable {
    Profile,
    Appearance,
}
