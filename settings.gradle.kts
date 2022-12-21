dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            val kotlin = "1.7.21"

            version(
                /* alias = */ "gradle-plugin-kotlin",
                /* version = */ kotlin
            )

            version(
                /* alias = */ "gradle-plugin-ksp",
                /* version = */ "$kotlin-1.0.8"
            )

            version(
                /* alias = */ "gradle-plugin-android",
                /* version = */ "7.3.1"
            )

            version(
                /* alias = */ "gradle-plugin-protobuf",
                /* version = */ "0.9.1"
            )

            version(
                /* alias = */ "gradle-plugin-versions",
                /* version = */ "0.44.0"
            )


            version(
                /* alias = */ "kotlinx-coroutines",
                /* version = */ "1.6.4"
            )

            version(
                /* alias = */ "kotlinx-serialization",
                /* version = */ "1.4.1"
            )

            version(
                /* alias = */ "protobuf",
                /* version = */ "3.21.10"
            )

            version(
                /* alias = */ "ktor",
                /* version = */ "2.1.3"
            )

            version(
                /* alias = */ "room",
                /* version = */ "2.5.0-beta02"
            )

            version(
                /* alias = */ "lifecycle",
                /* version = */ "2.6.0-alpha03"
            )

            version(
                /* alias = */ "navigation",
                /* version = */ "2.6.0-alpha04"
            )

            version(
                /* alias = */ "compose",
                /* version = */ "1.4.0-alpha02"
            )

            version(
                /* alias = */ "compose-compiler",
                /* version = */ "1.4.0-alpha02"
            )

            version(
                /* alias = */ "wear-compose",
                /* version = */ "1.0.0-alpha18"
            )

            version(
                /* alias = */ "material3",
                /* version = */ "1.0.1"
            )

            version(
                /* alias = */ "accompanist",
                /* version = */ "0.28.0"
            )

            version(
                /* alias = */ "hilt",
                /* version = */ "2.44.2"
            )

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
                    "accompanist-navigation-animation",
                    "accompanist-navigation-material",
                    "accompanist-systemuicontroller",
                    "accompanist-adaptive",
                    "accompanist-placeholder"
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

                    "lifecycle-runtime-ktx",
                    "lifecycle-viewmodel-ktx",
                    "lifecycle-viewmodel-compose",
                    "lifecycle-viewmodel-savedstate",
                    "lifecycle-livedata-ktx",

                    // compose
                    "ui",
                    "ui-viewbinding",
                    "ui-tooling",
                    "ui-tooling-preview",
                    "ui-util",
                    "material-icons-core",
                    "material-icons-extended",
                    "foundation",
                    "runtime-livedata",
                    "ui-text-google-fonts",
                    "constraintlayout",
                    "metrics-performance",
                    "compose-foundation",
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
                    "compose-material"
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
            ).version("1.1.0-alpha01")

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
                /* alias = */ "collection-ktx",
                /* group = */ "androidx.collection",
                /* artifact = */ "collection-ktx"
            ).version("1.2.0")

            library(
                /* alias = */ "core-ktx",
                /* group = */ "androidx.core",
                /* artifact = */ "core-ktx"
            ).version("1.9.0")

            library(
                /* alias = */ "core-google-shortcuts",
                /* group = */ "androidx.core",
                /* artifact = */ "core-google-shortcuts"
            ).version("1.1.0")

            library(
                /* alias = */ "appcompat",
                /* group = */ "androidx.appcompat",
                /* artifact = */ "appcompat"
            ).version("1.7.0-alpha01")

            library(
                /* alias = */ "window",
                /* group = */ "androidx.window",
                /* artifact = */ "window"
            ).version("1.1.0-alpha04")

            library(
                /* alias = */ "activity-ktx",
                /* group = */ "androidx.activity",
                /* artifact = */ "activity-ktx"
            ).version("1.7.0-alpha02")

            library(
                /* alias = */ "activity-compose",
                /* group = */ "androidx.activity",
                /* artifact = */ "activity-compose"
            ).version("1.7.0-alpha02")

            library(
                /* alias = */ "fragment-ktx",
                /* group = */ "androidx.fragment",
                /* artifact = */ "fragment-ktx"
            ).version("1.6.0-alpha03")

            library(
                /* alias = */ "palette-ktx",
                /* group = */ "androidx.palette",
                /* artifact = */ "palette-ktx"
            ).version("1.0.0")

            library(
                /* alias = */ "work-runtime-ktx",
                /* group = */ "androidx.work",
                /* artifact = */ "work-runtime-ktx"
            ).version("2.8.0-beta02")

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
            ).version("1.2.0-alpha07")

            library(
                /* alias = */ "lifecycle-runtime-ktx",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-runtime-ktx"
            ).versionRef("lifecycle")

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
                /* alias = */ "lifecycle-livedata-ktx",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-livedata-ktx"
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
                "hilt-navigation-compose",
                "androidx.hilt",
                "hilt-navigation-compose"
            ).version("1.0.0")

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
            ).version("1.8.0-alpha02")

            library(
                /* alias = */ "material3",
                /* group = */ "androidx.compose.material3",
                /* artifact = */ "material3"
            ).versionRef("material3")

            library(
                // cannot have `class` as alias postfix because gradle fails
                /* alias = */ "material3-window-size",
                /* group = */ "androidx.compose.material3",
                /* artifact = */ "material3-window-size-class"
            ).versionRef("material3")

            library(
                /* alias = */ "ui",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui"
            ).versionRef("compose")

            library(
                /* alias = */ "ui-viewbinding",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-viewbinding"
            ).versionRef("compose")

            library(
                /* alias = */ "ui-tooling",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-tooling"
            ).versionRef("compose")

            library(
                /* alias = */ "ui-tooling-preview",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-tooling-preview"
            ).versionRef("compose")

            library(
                /* alias = */ "ui-util",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-util"
            ).versionRef("compose")

            library(
                /* alias = */ "compose-material",
                /* group = */ "androidx.compose.material",
                /* artifact = */ "material"
            ).versionRef("compose")

            library(
                /* alias = */ "material-icons-core",
                /* group = */ "androidx.compose.material",
                /* artifact = */ "material-icons-core"
            ).versionRef("compose")

            library(
                /* alias = */ "material-icons-extended",
                /* group = */ "androidx.compose.material",
                /* artifact = */ "material-icons-extended"
            ).versionRef("compose")

            library(
                /* alias = */ "foundation",
                /* group = */ "androidx.compose.foundation",
                /* artifact = */ "foundation"
            ).versionRef("compose")

            library(
                /* alias = */ "runtime-livedata",
                /* group = */ "androidx.compose.runtime",
                /* artifact = */ "runtime-livedata"
            ).versionRef("compose")

            library(
                /* alias = */ "ui-text-google-fonts",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-text-google-fonts"
            ).version("1.4.0-alpha02")

            library(
                /* alias = */ "constraintlayout",
                /* group = */ "androidx.constraintlayout",
                /* artifact = */ "constraintlayout-compose"
            ).version("1.1.0-alpha04")

            library(
                /* alias = */ "metrics-performance",
                /* group = */ "androidx.metrics",
                /* artifact = */ "metrics-performance"
            ).version("1.0.0-alpha03")

            library(
                /* alias = */ "compose-foundation",
                /* group = */ "androidx.wear.compose",
                /* artifact = */ "compose-foundation"
            ).version("1.1.0-rc01")

            library(
                /* alias = */ "compose-material-wear",
                /* group = */ "androidx.wear.compose",
                /* artifact = */ "compose-material"
            ).version("1.1.0-rc01")

            library(
                /* alias = */ "compose-navigation-wear",
                /* group = */ "androidx.wear.compose",
                /* artifact = */ "compose-navigation"
            ).version("1.1.0-rc01")

            library(
                /* alias = */ "accompanist-navigation-animation",
                /* group = */ "com.google.accompanist",
                /* artifact = */ "accompanist-navigation-animation"
            ).versionRef("accompanist")

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
                /* alias = */ "slf4j-simple",
                /* group = */ "org.slf4j",
                /* artifact = */ "slf4j-simple"
            ).version("2.0.5")

            library(
                /* alias = */ "ui-test-junit4",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-test-junit4"
            ).version("1.4.0-alpha02")

            library(
                /* alias = */ "ui-test-manifest",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-test-manifest"
            ).version("1.4.0-alpha02")

            library(
                /* alias = */ "junit",
                /* group = */ "junit",
                /* artifact = */ "junit"
            ).version("4.13.2")

            library(
                /* alias = */ "junit-ext",
                /* group = */ "androidx.test.ext",
                /* artifact = */ "junit"
            ).version("1.1.4")

            library(
                /* alias = */ "espresso-core",
                /* group = */ "androidx.test.espresso",
                /* artifact = */ "espresso-core"
            ).version("3.5.0")
        }
    }
}

rootProject.name = "Hacker News"
include(":app")
include(":wear")
include(":common:view")
include(":common:domain")
include(":common:data")
