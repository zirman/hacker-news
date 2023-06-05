@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            val kotlin = "1.8.21"

            version(
                /* alias = */ "kotlin",
                /* version = */ kotlin
            )

            version(
                /* alias = */ "gradle-plugin-ksp",
                /* version = */ "$kotlin-1.0.11"
            )

            version(
                /* alias = */ "gradle-plugin-android",
                /* version = */ "8.0.2"
            )

            version(
                /* alias = */ "gradle-plugin-protobuf",
                /* version = */ "0.9.3"
            )

            version(
                /* alias = */ "kotlinx-coroutines",
                /* version = */ "1.7.1"
            )

            version(
                /* alias = */ "kotlinx-serialization",
                /* version = */ "1.5.1"
            )

            version(
                /* alias = */ "protobuf",
                /* version = */ "3.23.2"
            )

            version(
                /* alias = */ "ktor",
                /* version = */ "2.3.0"
            )

            version(
                /* alias = */ "room",
                /* version = */ "2.5.1"
            )

            version(
                /* alias = */ "lifecycle",
                /* version = */ "2.6.1"
            )

            version(
                /* alias = */ "navigation",
                /* version = */ "2.7.0-alpha01"
            )

            version(
                /* alias = */ "compose-compiler",
                /* version = */ "1.4.7"
            )

            version(
                /* alias = */ "wear-compose",
                /* version = */ "1.0.0-alpha18"
            )

            version(
                /* alias = */ "accompanist",
                /* version = */ "0.31.2-alpha"
            )

            version(
                /* alias = */ "hilt",
                /* version = */ "2.46.1"
            )

            version(
                /* alias = */ "google-services",
                /* version = */ "4.3.15"
            )

            version(
                /* alias = */ "firebase-crashlytics-gradle",
                /* version = */ "2.9.5"
            )

            plugin(
                /* alias = */ "kotlin",
                /* id = */ "org.jetbrains.kotlin.android"
            ).versionRef("kotlin")

            plugin(
                /* alias = */ "parcelize",
                /* id = */ "org.jetbrains.kotlin.plugin.parcelize"
            ).versionRef("kotlin")

            plugin(
                /* alias = */ "kapt",
                /* id = */ "org.jetbrains.kotlin.kapt"
            ).versionRef("kotlin")

            plugin(
                /* alias = */ "hilt",
                /* id = */ "com.google.dagger.hilt.android"
            ).versionRef("hilt")

            plugin(
                /* alias = */ "versions",
                /* id = */ "com.github.ben-manes.versions"
            ).version("0.46.0")

            bundle(
                /* alias = */ "kotlinx",
                /* aliases = */ listOf(
                    "kotlinx-coroutines-android",
                    "kotlinx-coroutines-play-services",

                    "kotlinx-serialization-json",
                    "kotlinx-serialization-protobuf",

                    "kotlinx-datetime"
                )
            )

            bundle(
                /* alias = */ "ktor",
                /* aliases = */ listOf(
                    "ktor-client-core",
                    "ktor-client-android",
                    "ktor-client-logging",
                    "ktor-client-content-negotiation",
                    "ktor-serialization-kotlinx-json"
                )
            )

            bundle(
                /* alias = */ "google",
                /* aliases = */ listOf(
                    "ksp-symbol-processing-api",
                    "protobuf-kotlin-lite"
                )
            )

            bundle(
                /* alias = */ "google-app",
                /* aliases = */ listOf(
                    "accompanist-navigation-material", // bottomSheet destination for navigation compose
                    "accompanist-systemuicontroller", // control system ui from compose
                    "accompanist-adaptive", // handle foldable devices
                    "accompanist-placeholder", // placeholder loading animation
                )
            )

            bundle(
                /* alias = */ "google-wear",
                /* aliases = */ listOf(
                    "play-services-wearable",
                )
            )

            bundle(
                /* alias = */ "androidx",
                /* aliases = */ listOf(
                    "core-ktx",
                    "appcompat",
                    "window",
                    "activity-ktx",
                    "activity-compose",
                    "fragment-ktx",
                    "palette-ktx",
                    "work-runtime-ktx",
                    "preference-ktx",
                    // "slice-builders-ktx",
                    "datastore",
                    "benchmark-junit4",

                    "room-runtime",
                    "room-ktx",
                    "room-paging",

                    "lifecycle-process",
                    "lifecycle-runtime-ktx",
                    "lifecycle-runtime-compose",
                    "lifecycle-viewmodel-ktx",
                    "lifecycle-viewmodel-compose",
                    "lifecycle-viewmodel-savedstate",
                )
            )

            bundle(
                /* alias = */ "androidx-app",
                /* aliases = */ listOf(
                    "core-google-shortcuts",

                    "navigation-runtime-ktx",
                    "navigation-ui-ktx",
                    "navigation-compose",

                    "material",
                    "material3",
                    "material3-window-size",

                    // compose
                    "compose-material",
                )
            )

            bundle(
                /* alias = */ "androidx-wear",
                /* aliases = */ listOf(
                    "compose-material-wear",
                    "compose-navigation-wear"
                )
            )

            library(
                /* alias = */ "kotlinx-coroutines-android",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-coroutines-android"
            ).versionRef("kotlinx-coroutines")

            library(
                /* alias = */ "kotlinx-coroutines-play-services",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-coroutines-play-services"
            ).versionRef("kotlinx-coroutines")

            library(
                /* alias = */ "kotlinx-coroutines-test",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-coroutines-test"
            ).version("kotlinx-coroutines")

            library(
                /* alias = */ "kotlinx-serialization-json",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-serialization-json"
            ).versionRef("kotlinx-serialization")

            library(
                /* alias = */ "kotlinx-serialization-protobuf",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-serialization-protobuf"
            ).versionRef("kotlinx-serialization")

            library(
                /* alias = */ "kotlinx-datetime",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-datetime"
            ).version("0.4.0")

            library(
                /* alias = */ "ktor-client-core",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-client-core"
            ).versionRef("ktor")

            library(
                /* alias = */ "ktor-client-android",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-client-android"
            ).versionRef("ktor")

            library(
                /* alias = */ "ktor-client-logging",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-client-logging"
            ).versionRef("ktor")

            library(
                /* alias = */ "ktor-client-content-negotiation",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-client-content-negotiation"
            ).versionRef("ktor")

            library(
                /* alias = */ "ktor-serialization-kotlinx-json",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-serialization-kotlinx-json"
            ).versionRef("ktor")

            library(
                /* alias = */ "play-services-wearable",
                /* group = */ "com.google.android.gms",
                /* artifact = */ "play-services-wearable"
            ).version("18.0.0")

            library(
                /* alias = */ "protobuf-kotlin-lite",
                /* group = */ "com.google.protobuf",
                /* artifact = */ "protobuf-kotlin-lite"
            ).versionRef("protobuf")

            library(
                /* alias = */ "ksp-symbol-processing-api",
                /* group = */ "com.google.devtools.ksp",
                /* artifact = */ "symbol-processing-api"
            ).versionRef("gradle-plugin-ksp")

            library(
                /* alias = */ "datastore",
                /* group = */ "androidx.datastore",
                /* artifact = */ "datastore"
            ).version("1.1.0-alpha04")

            library(
                /* alias = */ "room-runtime",
                /* group = */ "androidx.room",
                /* artifact = */ "room-runtime"
            ).versionRef("room")

            library(
                /* alias = */ "room-ktx",
                /* group = */ "androidx.room",
                /* artifact = */ "room-ktx"
            ).versionRef("room")

            library(
                /* alias = */ "room-paging",
                /* group = */ "androidx.room",
                /* artifact = */ "room-paging"
            ).versionRef("room")

            library(
                /* alias = */ "room-compiler",
                /* group = */ "androidx.room",
                /* artifact = */ "room-compiler"
            ).versionRef("room")

            library(
                /* alias = */ "window",
                /* group = */ "androidx.window",
                /* artifact = */ "window"
            ).version("1.1.0-rc01")

            library(
                /* alias = */ "appcompat",
                /* group = */ "androidx.appcompat",
                /* artifact = */ "appcompat"
            ).version("1.7.0-alpha02")

            library(
                /* alias = */ "core-google-shortcuts",
                /* group = */ "androidx.core",
                /* artifact = */ "core-google-shortcuts"
            ).version("1.1.0")

            library(
                /* alias = */ "collection-ktx",
                /* group = */ "androidx.collection",
                /* artifact = */ "collection-ktx"
            ).version("1.2.0")

            library(
                /* alias = */ "core-ktx",
                /* group = */ "androidx.core",
                /* artifact = */ "core-ktx"
            ).version("1.10.1")

            library(
                /* alias = */ "activity-ktx",
                /* group = */ "androidx.activity",
                /* artifact = */ "activity-ktx"
            ).version("1.7.2")

            library(
                /* alias = */ "fragment-ktx",
                /* group = */ "androidx.fragment",
                /* artifact = */ "fragment-ktx"
            ).version("1.6.0-rc01")

            library(
                /* alias = */ "activity-compose",
                /* group = */ "androidx.activity",
                /* artifact = */ "activity-compose"
            ).version("1.7.2")

            library(
                /* alias = */ "palette-ktx",
                /* group = */ "androidx.palette",
                /* artifact = */ "palette-ktx"
            ).version("1.0.0")

            library(
                /* alias = */ "work-runtime-ktx",
                /* group = */ "androidx.work",
                /* artifact = */ "work-runtime-ktx"
            ).version("2.8.1")

            library(
                /* alias = */ "preference-ktx",
                /* group = */ "androidx.preference",
                /* artifact = */ "preference-ktx"
            ).version("1.2.0")

            library(
                /* alias = */ "slice-builders-ktx",
                /* group = */ "androidx.slice",
                /* artifact = */ "slice-builders-ktx"
            ).version("1.0.0-alpha08")

            library(
                /* alias = */ "benchmark-junit4",
                /* group = */ "androidx.benchmark",
                /* artifact = */ "benchmark-junit4"
            ).version("1.2.0-alpha14")

            library(
                /* alias = */ "lifecycle-process",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-process"
            ).versionRef("lifecycle")

            library(
                /* alias = */ "lifecycle-runtime-ktx",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-runtime-ktx"
            ).withoutVersion()

            library(
                /* alias = */ "lifecycle-runtime-compose",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-runtime-compose"
            ).withoutVersion()

            library(
                /* alias = */ "lifecycle-viewmodel-ktx",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-viewmodel-ktx"
            ).versionRef("lifecycle")

            library(
                /* alias = */ "lifecycle-viewmodel-compose",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-viewmodel-compose"
            ).versionRef("lifecycle")

            library(
                /* alias = */ "lifecycle-viewmodel-savedstate",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-viewmodel-savedstate"
            ).versionRef("lifecycle")

            library(
                /* alias = */ "navigation-runtime-ktx",
                /* group = */ "androidx.navigation",
                /* artifact = */ "navigation-runtime-ktx"
            ).versionRef("navigation")

            library(
                /* alias = */ "hilt-android",
                /* group = */ "com.google.dagger",
                /* artifact = */ "hilt-android"
            ).versionRef("hilt")

            library(
                /* alias = */ "hilt-android-compiler",
                /* group = */ "com.google.dagger",
                /* artifact = */ "hilt-android-compiler"
            ).versionRef("hilt")

            library(
                /* alias = */ "hilt-navigation-compose",
                /* group = */ "androidx.hilt",
                /* artifact = */ "hilt-navigation-compose"
            ).version("1.1.0-alpha01")

            library(
                /* alias = */ "navigation-ui-ktx",
                /* group = */ "androidx.navigation",
                /* artifact = */ "navigation-ui-ktx"
            ).versionRef("navigation")

            library(
                /* alias = */ "navigation-compose",
                /* group = */ "androidx.navigation",
                /* artifact = */ "navigation-compose"
            ).versionRef("navigation")

            library(
                /* alias = */ "material",
                /* group = */ "com.google.android.material",
                /* artifact = */ "material"
            ).version("1.9.0-beta01")

            library(
                /* alias = */ "compose-bom",
                /* group = */ "androidx.compose",
                /* artifact = */ "compose-bom"
            ).version("2023.05.01")

            library(
                /* alias = */ "material3",
                /* group = */ "androidx.compose.material3",
                /* artifact = */ "material3"
            ).withoutVersion()

            library(
                // cannot have `class` as alias postfix because gradle fails
                /* alias = */ "material3-window-size",
                /* group = */ "androidx.compose.material3",
                /* artifact = */ "material3-window-size-class"
            ).withoutVersion()

            library(
                /* alias = */ "ui",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui"
            ).withoutVersion()

            library(
                /* alias = */ "ui-viewbinding",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-viewbinding"
            ).withoutVersion()

            library(
                /* alias = */ "ui-tooling",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-tooling"
            ).withoutVersion()

            library(
                /* alias = */ "ui-tooling-data",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-tooling-data"
            ).withoutVersion()

            library(
                /* alias = */ "ui-tooling-preview",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-tooling-preview"
            ).withoutVersion()

            library(
                /* alias = */ "ui-util",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-util"
            ).withoutVersion()

            library(
                /* alias = */ "compose-material",
                /* group = */ "androidx.compose.material",
                /* artifact = */ "material"
            ).withoutVersion()

            library(
                /* alias = */ "material-icons-core",
                /* group = */ "androidx.compose.material",
                /* artifact = */ "material-icons-core"
            ).withoutVersion()

            library(
                /* alias = */ "material-icons-extended",
                /* group = */ "androidx.compose.material",
                /* artifact = */ "material-icons-extended"
            ).withoutVersion()

            library(
                /* alias = */ "foundation",
                /* group = */ "androidx.compose.foundation",
                /* artifact = */ "foundation"
            ).withoutVersion()

            library(
                /* alias = */ "ui-text-google-fonts",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-text-google-fonts"
            ).withoutVersion()

            bundle(
                "androidx.compose",
                listOf(
                    // compose
                    "ui",
                    "ui-viewbinding",
                    "ui-tooling",
                    "ui-tooling-data",
                    "ui-tooling-preview",
                    "constraintlayout",
                    "runtime-tracing",
                    "metrics-performance",
                    "ui-util",
                    "compose-foundation",
                    "material-icons-core",
                    "material-icons-extended",
                    "foundation",
                    "ui-text-google-fonts"
                )
            )

            library(
                /* alias = */ "runtime-tracing",
                /* group = */ "androidx.compose.runtime",
                /* artifact = */ "runtime-tracing"
            ).version("1.0.0-alpha03")

            library(
                /* alias = */ "ui-test-junit4",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-test-junit4"
            ).version("1.4.3")

            library(
                /* alias = */ "ui-test-manifest",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-test-manifest"
            ).withoutVersion()

            library(
                /* alias = */ "constraintlayout",
                /* group = */ "androidx.constraintlayout",
                /* artifact = */ "constraintlayout-compose"
            ).version("1.1.0-alpha10")

            library(
                /* alias = */ "metrics-performance",
                /* group = */ "androidx.metrics",
                /* artifact = */ "metrics-performance"
            ).version("1.0.0-alpha04")

            library(
                /* alias = */ "compose-foundation",
                /* group = */ "androidx.wear.compose",
                /* artifact = */ "compose-foundation"
            ).version("1.1.2")

            library(
                /* alias = */ "compose-material-wear",
                /* group = */ "androidx.wear.compose",
                /* artifact = */ "compose-material"
            ).version("1.1.2")

            library(
                /* alias = */ "compose-navigation-wear",
                /* group = */ "androidx.wear.compose",
                /* artifact = */ "compose-navigation"
            ).version("1.1.2")

            library(
                /* alias = */ "accompanist-navigation-material",
                /* group = */ "com.google.accompanist",
                /* artifact = */ "accompanist-navigation-material"
            ).versionRef("accompanist")

            library(
                /* alias = */ "accompanist-systemuicontroller",
                /* group = */ "com.google.accompanist",
                /* artifact = */ "accompanist-systemuicontroller"
            ).versionRef("accompanist")

            library(
                /* alias = */ "accompanist-adaptive",
                /* group = */ "com.google.accompanist",
                /* artifact = */ "accompanist-adaptive"
            ).versionRef("accompanist")

            library(
                /* alias = */ "accompanist-placeholder",
                /* group = */ "com.google.accompanist",
                /* artifact = */ "accompanist-placeholder"
            ).versionRef("accompanist")

            library(
                /* alias = */ "firebase-bom",
                /* group = */ "com.google.firebase",
                /* artifact = */ "firebase-bom"
            ).version("32.1.0")

            library(
                /* alias = */ "firebase-crashlytics",
                /* group = */ "com.google.firebase",
                /* artifact = */ "firebase-crashlytics"
            ).withoutVersion()

            library(
                /* alias = */ "firebase-analytics-ktx",
                /* group = */ "com.google.firebase",
                /* artifact = */ "firebase-analytics-ktx"
            ).withoutVersion()

            bundle(
                "firebase",
                listOf(
                    "firebase-crashlytics",
                    "firebase-analytics-ktx"
                )
            )

            library(
                /* alias = */ "slf4j-simple",
                /* group = */ "org.slf4j",
                /* artifact = */ "slf4j-simple"
            ).version("2.0.7")

            library(
                /* alias = */ "junit",
                /* group = */ "junit",
                /* artifact = */ "junit"
            ).version("4.13.2")

            library(
                /* alias = */ "junit-ext",
                /* group = */ "androidx.test.ext",
                /* artifact = */ "junit"
            ).version("1.1.5")

            library(
                /* alias = */ "espresso-core",
                /* group = */ "androidx.test.espresso",
                /* artifact = */ "espresso-core"
            ).version("3.5.1")
        }
    }
}

rootProject.name = "Hacker News"
include(":app")
include(":wear")
include(":common:injection")
include(":common:view")
include(":common:domain")
include(":common:data")
include(":common:util")
