dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            val kotlin = "1.6.21"

            version("gradle-plugin-kotlin", kotlin)
            version("gradle-plugin-ksp", "$kotlin-1.0.6")
            version("gradle-plugin-android", "7.2.1")
            version("gradle-plugin-protobuf", "0.8.18")
            version("gradle-plugin-versions", "0.42.0")

            version("kotlinx-coroutines", "1.6.2")
            version("kotlinx-serialization", "1.3.3")
            version("ktor", "2.0.2")
            version("room", "2.5.0-alpha02")
            version("lifecycle", "2.5.0-rc01")
            version("navigation", "2.5.0-rc01")
            version("compose", "1.2.0-beta03")
            version("material3", "1.0.0-alpha13")
            version("accompanist", "0.24.10-beta")

            bundle(
                "kotlinx",
                listOf(
                    "kotlinx-coroutines-core",
                    "kotlinx-coroutines-android",
                    "kotlinx-coroutines-play-services",

                    "kotlinx-serialization-json",
                    "kotlinx-serialization-protobuf",

                    "kotlinx-datetime"
                )
            )

            bundle(
                "ktor",
                listOf(
                    "ktor-client-core",
                    "ktor-client-android",
                    "ktor-client-logging",
                    "ktor-client-content-negotiation",
                    "ktor-serialization-kotlinx-json"
                )
            )

            bundle(
                "google",
                listOf(
                    "ksp-symbol-processing-api",
                    "protobuf-kotlin-lite",
                    "accompanist-navigation-animation",
                    "accompanist-navigation-material",
                    "accompanist-swiperefresh",
                    "accompanist-systemuicontroller",
                    "accompanist-placeholder"
                )
            )

            bundle(
                "androidx",
                listOf(
                    "core-ktx",
                    "appcompat",
                    "window",
                    "activity-ktx",
                    "activity-compose",
                    "fragment-ktx",
                    "palette-ktx",
                    "work-runtime-ktx",
                    "preference-ktx",
                    "slice-builders-ktx",
                    "datastore",
                    "benchmark-junit4",

                    "room-runtime",
                    "room-ktx",
                    "room-paging",

                    "lifecycle-runtime-ktx",
                    "lifecycle-viewmodel-ktx",
                    "lifecycle-viewmodel-compose",
                    "lifecycle-viewmodel-savedstate",
                    "lifecycle-livedata-ktx",

                    "navigation-runtime-ktx",
                    "navigation-ui-ktx",
                    "navigation-compose",

                    "material",
                    "material3",
                    "material3-window-size",

                    // compose
                    "ui",
                    "ui-viewbinding",
                    "ui-tooling",
                    "ui-tooling-preview",
                    "ui-util",
                    "material-compose",
                    "material-icons-core",
                    "material-icons-extended",
                    "foundation",
                    "runtime-livedata",
                    "constraintlayout"
                )
            )

            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core")
                .versionRef("kotlinx-coroutines")

            library(
                "kotlinx-coroutines-android",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-android"
            ).versionRef("kotlinx-coroutines")

            library(
                "kotlinx-coroutines-play-services",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-play-services"
            ).versionRef("kotlinx-coroutines")

            library(
                "kotlinx-serialization-json",
                "org.jetbrains.kotlinx",
                "kotlinx-serialization-json"
            ).versionRef("kotlinx-serialization")

            library(
                "kotlinx-serialization-protobuf",
                "org.jetbrains.kotlinx",
                "kotlinx-serialization-protobuf"
            ).versionRef("kotlinx-serialization")

            library("kotlinx-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime")
                .version("0.3.3")

            library("ktor-client-core", "io.ktor", "ktor-client-core")
                .versionRef("ktor")

            library("ktor-client-android", "io.ktor", "ktor-client-android")
                .versionRef("ktor")

            library("ktor-client-logging", "io.ktor", "ktor-client-logging")
                .versionRef("ktor")

            library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation")
                .versionRef("ktor")

            library("ktor-serialization-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json")
                .versionRef("ktor")

            library("protobuf-kotlin-lite", "com.google.protobuf", "protobuf-kotlin-lite")
                .version("3.21.1")

            library("ksp-symbol-processing-api", "com.google.devtools.ksp", "symbol-processing-api")
                .versionRef("gradle-plugin-ksp")

            library("datastore", "androidx.datastore", "datastore")
                .version("1.0.0")

            library("room-runtime", "androidx.room", "room-runtime")
                .versionRef("room")

            library("room-ktx", "androidx.room", "room-ktx")
                .versionRef("room")

            library("room-paging", "androidx.room", "room-paging")
                .versionRef("room")

            library("collection-ktx", "androidx.collection", "collection-ktx")
                .version("1.2.0")

            library("core-ktx", "androidx.core", "core-ktx")
                .version("1.9.0-alpha04")

            library("appcompat", "androidx.appcompat", "appcompat")
                .version("1.6.0-alpha04")

            library("window", "androidx.window", "window")
                .version("1.1.0-alpha02")

            library("activity-ktx", "androidx.activity", "activity-ktx")
                .version("1.6.0-alpha04")

            library("activity-compose", "androidx.activity", "activity-compose")
                .version("1.6.0-alpha03")

            library("fragment-ktx", "androidx.fragment", "fragment-ktx")
                .version("1.5.0-rc01")

            library("palette-ktx", "androidx.palette", "palette-ktx")
                .version("1.0.0")

            library("work-runtime-ktx", "androidx.work", "work-runtime-ktx")
                .version("2.7.1")

            library("preference-ktx", "androidx.preference", "preference-ktx")
                .version("1.2.0")

            library("slice-builders-ktx", "androidx.slice", "slice-builders-ktx")
                .version("1.0.0-alpha08")

            library("benchmark-junit4", "androidx.benchmark", "benchmark-junit4")
                .version("1.1.0-rc03")

            library("lifecycle-runtime-ktx", "androidx.lifecycle", "lifecycle-runtime-ktx")
                .versionRef("lifecycle")

            library("lifecycle-viewmodel-ktx", "androidx.lifecycle", "lifecycle-viewmodel-ktx")
                .versionRef("lifecycle")

            library(
                "lifecycle-viewmodel-compose",
                "androidx.lifecycle",
                "lifecycle-viewmodel-compose"
            ).versionRef("lifecycle")

            library(
                "lifecycle-viewmodel-savedstate",
                "androidx.lifecycle",
                "lifecycle-viewmodel-savedstate"
            ).versionRef("lifecycle")

            library("lifecycle-livedata-ktx", "androidx.lifecycle", "lifecycle-livedata-ktx")
                .versionRef("lifecycle")

            library("navigation-runtime-ktx", "androidx.navigation", "navigation-runtime-ktx")
                .versionRef("navigation")

            library("navigation-ui-ktx", "androidx.navigation", "navigation-ui-ktx")
                .versionRef("navigation")

            library("navigation-compose", "androidx.navigation", "navigation-compose")
                .versionRef("navigation")

            library("material", "com.google.android.material", "material")
                .version("1.7.0-alpha02")

            library("material3", "androidx.compose.material3", "material3")
                .versionRef("material3")

            library(
                // cannot have `class` as alias postfix because gradle fails
                "material3-window-size",
                "androidx.compose.material3",
                "material3-window-size-class"
            ).versionRef("material3")

            library("ui", "androidx.compose.ui", "ui")
                .versionRef("compose")

            library("ui-viewbinding", "androidx.compose.ui", "ui-viewbinding")
                .versionRef("compose")

            library("ui-tooling", "androidx.compose.ui", "ui-tooling")
                .versionRef("compose")

            library("ui-tooling-preview", "androidx.compose.ui", "ui-tooling-preview")
                .versionRef("compose")

            library("ui-util", "androidx.compose.ui", "ui-util")
                .versionRef("compose")

            library("material-compose", "androidx.compose.material", "material")
                .versionRef("compose")

            library("material-icons-core", "androidx.compose.material", "material-icons-core")
                .versionRef("compose")

            library(
                "material-icons-extended",
                "androidx.compose.material",
                "material-icons-extended"
            ).versionRef("compose")

            library("foundation", "androidx.compose.foundation", "foundation")
                .versionRef("compose")

            library("runtime-livedata", "androidx.compose.runtime", "runtime-livedata")
                .versionRef("compose")

            library("constraintlayout", "androidx.constraintlayout", "constraintlayout-compose")
                .version("1.1.0-alpha02")

            library(
                "accompanist-navigation-animation",
                "com.google.accompanist",
                "accompanist-navigation-animation"
            ).versionRef("accompanist")

            library(
                "accompanist-navigation-material",
                "com.google.accompanist",
                "accompanist-navigation-material"
            ).versionRef("accompanist")

            library(
                "accompanist-swiperefresh",
                "com.google.accompanist",
                "accompanist-swiperefresh"
            ).versionRef("accompanist")

            library(
                "accompanist-systemuicontroller",
                "com.google.accompanist",
                "accompanist-systemuicontroller"
            ).versionRef("accompanist")

            library("accompanist-placeholder", "com.google.accompanist", "accompanist-placeholder")
                .versionRef("accompanist")
        }
    }
}
rootProject.name = "Hacker News"
include(":app")
