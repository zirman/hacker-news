dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("kotlinx-coroutines", "1.6.2")
            version("kotlinx-serialization", "1.3.3")
            version("kotlinx-datetime", "0.3.3")
            version("ktor", "2.0.2")
            version("protobuf", "3.21.1")
            version("datastore", "1.0.0")
            version("room", "2.5.0-alpha01")
            version("collection-ktx", "1.2.0")
            version("core-ktx", "1.9.0-alpha04")
            version("appcompat", "1.6.0-alpha04")
            version("window", "1.1.0-alpha02")
            version("activity-ktx", "1.6.0-alpha04")
            version("activity-compose", "1.6.0-alpha03")
            version("fragment-ktx", "1.5.0-rc01")
            version("lifecycle", "2.5.0-rc01")
            version("navigation", "2.5.0-rc01")
            version("material", "1.7.0-alpha02")
            version("material3", "1.0.0-alpha12")
            version("compose", "1.2.0-beta02")
            version("constraintlayout", "1.1.0-alpha02")
            version("accompanist", "0.24.9-beta")
            version("benchmark-junit4", "1.1.0-rc02")

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
                    "constraintlayout",
                )
            )

            library(
                "kotlinx-coroutines-core",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-core"
            ).versionRef("kotlinx-coroutines")

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

            library(
                "kotlinx-datetime",
                "org.jetbrains.kotlinx",
                "kotlinx-datetime"
            ).versionRef("kotlinx-datetime")

            library(
                "ktor-client-core",
                "io.ktor",
                "ktor-client-core"
            ).versionRef("ktor")

            library(
                "ktor-client-android",
                "io.ktor",
                "ktor-client-android"
            ).versionRef("ktor")

            library(
                "ktor-client-logging",
                "io.ktor",
                "ktor-client-logging"
            ).versionRef("ktor")

            library(
                "ktor-client-content-negotiation",
                "io.ktor",
                "ktor-client-content-negotiation"
            ).versionRef("ktor")

            library(
                "ktor-serialization-kotlinx-json",
                "io.ktor",
                "ktor-serialization-kotlinx-json"
            ).versionRef("ktor")

            library(
                "protobuf-kotlin-lite",
                "com.google.protobuf",
                "protobuf-kotlin-lite"
            ).versionRef("protobuf")

            library(
                "datastore",
                "androidx.datastore",
                "datastore"
            ).versionRef("datastore")

            library(
                "room-runtime",
                "androidx.room",
                "room-runtime"
            ).versionRef("room")

            library(
                "room-ktx",
                "androidx.room",
                "room-ktx"
            ).versionRef("room")

            library(
                "room-paging",
                "androidx.room",
                "room-paging"
            ).versionRef("room")

            library(
                "collection-ktx",
                "androidx.collection",
                "collection-ktx"
            ).versionRef("collection-ktx")

            library(
                "core-ktx",
                "androidx.core",
                "core-ktx"
            ).versionRef("core-ktx")

            library(
                "appcompat",
                "androidx.appcompat",
                "appcompat"
            ).versionRef("appcompat")

            library(
                "window",
                "androidx.window",
                "window"
            ).versionRef("window")

            library(
                "activity-ktx",
                "androidx.activity",
                "activity-ktx"
            ).versionRef("activity-ktx")

            library(
                "activity-compose",
                "androidx.activity",
                "activity-compose"
            ).versionRef("activity-compose")

            library(
                "fragment-ktx",
                "androidx.fragment",
                "fragment-ktx"
            ).versionRef("fragment-ktx")

            library(
                "benchmark-junit4",
                "androidx.benchmark",
                "benchmark-junit4"
            ).versionRef("benchmark-junit4")

            library(
                "lifecycle-runtime-ktx",
                "androidx.lifecycle",
                "lifecycle-runtime-ktx"
            ).versionRef("lifecycle")

            library(
                "lifecycle-viewmodel-ktx",
                "androidx.lifecycle",
                "lifecycle-viewmodel-ktx"
            ).versionRef("lifecycle")

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

            library(
                "lifecycle-livedata-ktx",
                "androidx.lifecycle",
                "lifecycle-livedata-ktx"
            ).versionRef("lifecycle")

            library(
                "navigation-runtime-ktx",
                "androidx.navigation",
                "navigation-runtime-ktx"
            ).versionRef("navigation")

            library(
                "navigation-ui-ktx",
                "androidx.navigation",
                "navigation-ui-ktx"
            ).versionRef("navigation")

            library(
                "navigation-compose",
                "androidx.navigation",
                "navigation-compose"
            ).versionRef("navigation")

            library(
                "material",
                "com.google.android.material",
                "material"
            ).versionRef("material")

            library(
                "material3",
                "androidx.compose.material3",
                "material3"
            ).versionRef("material3")

            library(
                "ui",
                "androidx.compose.ui",
                "ui"
            ).versionRef("compose")

            library(
                "ui-viewbinding",
                "androidx.compose.ui",
                "ui-viewbinding"
            ).versionRef("compose")

            library(
                "ui-tooling",
                "androidx.compose.ui",
                "ui-tooling"
            ).versionRef("compose")

            library(
                "ui-tooling-preview",
                "androidx.compose.ui",
                "ui-tooling-preview"
            ).versionRef("compose")

            library(
                "ui-util",
                "androidx.compose.ui",
                "ui-util"
            ).versionRef("compose")

            library(
                "material-compose",
                "androidx.compose.material",
                "material"
            ).versionRef("compose")

            library(
                "material-icons-core",
                "androidx.compose.material",
                "material-icons-core"
            ).versionRef("compose")

            library(
                "material-icons-extended",
                "androidx.compose.material",
                "material-icons-extended"
            ).versionRef("compose")

            library(
                "foundation",
                "androidx.compose.foundation",
                "foundation"
            ).versionRef("compose")

            library(
                "runtime-livedata",
                "androidx.compose.runtime",
                "runtime-livedata"
            ).versionRef("compose")

            library(
                "constraintlayout",
                "androidx.constraintlayout",
                "constraintlayout-compose"
            ).versionRef("constraintlayout")

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

            library(
                "accompanist-placeholder",
                "com.google.accompanist",
                "accompanist-placeholder"
            ).versionRef("accompanist")
        }
    }
}
rootProject.name = "Hacker News"
include(":app")
