@file:OptIn(DelicateMetroGradleApi::class)

import dev.zacsweers.metro.gradle.DelicateMetroGradleApi

plugins {
    id("kotlin-android-app")
}
dependencies {
    implementation(project(":view"))
    implementation(libs.bundles.wear)
}
android {
    val appId = "com.monoid.hackernews.wear"
    namespace = appId
    defaultConfig {
        applicationId = appId
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "sn13p9hDhpAU"
            keyAlias = "release"
            keyPassword = "h8G8xDZYuceM"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                project.file("proguard-rules.pro"),
            )
        }
    }
}
