package com.monoid.hackernews.common.view

enum class Platform { Android, IOS, Desktop, Web }

expect val currentPlatform: Platform
