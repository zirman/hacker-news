package com.monoid.hackernews.common.view

enum class Platform { Android, IOS, Desktop }

expect val currentPlatform: Platform
