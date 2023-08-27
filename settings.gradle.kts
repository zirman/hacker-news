@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            val kotlin = "1.9.0"

            version(
                /* alias = */ "kotlin",
                /* version = */ kotlin
            )

            version(
                /* alias = */ "android",
                /* version = */ "8.2.0-beta01"
            )

            version(
                /* alias = */ "ksp",
                /* version = */ "$kotlin-1.0.13"
            )

            version(
                /* alias = */ "kotlinxCoroutines",
                /* version = */ "1.7.3"
            )

            version(
                /* alias = */ "kotlinxSerialization",
                /* version = */ "1.6.0-RC"
            )

            version(
                /* alias = */ "protobuf",
                /* version = */ "3.24.1"
            )

            version(
                /* alias = */ "ktor",
                /* version = */ "2.3.3"
            )

            version(
                /* alias = */ "room",
                /* version = */ "2.5.2"
            )

            version(
                /* alias = */ "lifecycle",
                /* version = */ "2.6.1"
            )

            version(
                /* alias = */ "navigation",
                /* version = */ "2.7.0-beta01"
            )

            version(
                /* alias = */ "composeCompiler",
                /* version = */ "1.5.1"
            )

            version(
                /* alias = */ "accompanist",
                /* version = */ "0.31.6-rc"
            )

            version(
                /* alias = */ "hilt",
                /* version = */ "2.47"
            )

            version(
                /* alias = */ "wearCompose",
                /* version = */ "1.2.0"
            )

            plugin(
                /* alias = */ "kotlinAndroid",
                /* id = */ "org.jetbrains.kotlin.android"
            ).versionRef("kotlin")

            plugin(
                /* alias = */ "kotlinxSerialization",
                /* id = */ "org.jetbrains.kotlin.plugin.serialization"
            ).versionRef("kotlin")

            plugin(
                /* alias = */ "kotlinxParcelize",
                /* id = */ "kotlin-parcelize"
            ).versionRef("kotlin")

            plugin(
                /* alias = */ "kapt",
                /* id = */ "org.jetbrains.kotlin.kapt"
            ).versionRef("kotlin")

            plugin(
                /* alias = */ "androidApplication",
                /* id = */ "com.android.application"
            ).versionRef("android")

            plugin(
                /* alias = */ "androidLibrary",
                /* id = */ "com.android.library"
            ).versionRef("android")

            plugin(
                /* alias = */ "ksp",
                /* id = */ "com.google.devtools.ksp"
            ).versionRef("ksp")

            plugin(
                /* alias = */ "hilt",
                /* id = */ "com.google.dagger.hilt.android"
            ).versionRef("hilt")

            plugin(
                /* alias = */ "protobuf",
                /* id = */ "com.google.protobuf"
            ).version("0.9.4")

            plugin(
                /* alias = */ "googlePlayServices",
                /* id = */ "com.google.gms.google-services"
            ).version("4.3.15")

            plugin(
                /* alias = */ "crashlytics",
                /* id = */ "com.google.firebase.crashlytics"
            ).version("2.9.8")

            plugin(
                /* alias = */ "firebasePerf",
                /* id = */ "com.google.firebase.firebase-perf"
            ).version("1.4.2")

            plugin(
                /* alias = */ "versions",
                /* id = */ "com.github.ben-manes.versions"
            ).version("0.47.0")

            bundle(
                /* alias = */ "kotlinx",
                /* aliases = */ listOf(
                    "kotlinxCoroutinesAndroid",
                    "kotlinxCoroutinesPlayServices",

                    "kotlinxSerializationJson",
                    "kotlinxSerializationProtobuf",

                    "kotlinxCollectionsImmutable",
                    "kotlinxDatetime"
                )
            )

            bundle(
                /* alias = */ "ktor",
                /* aliases = */ listOf(
                    "ktorClientCore",
                    "ktorClientAndroid",
                    "ktorClientLogging",
                    "ktorClientContentNegotiation",
                    "ktorSerializationKotlinxJson"
                )
            )

            bundle(
                /* alias = */ "google",
                /* aliases = */ listOf(
                    "kspSymbolProcessingApi",
                    "protobufKotlinLite"
                )
            )

            bundle(
                /* alias = */ "googleApp",
                /* aliases = */ listOf(
                    "accompanistNavigationMaterial", // bottomSheet destination for navigation compose
                    "accompanistAdaptive", // handle foldable devices
                    "accompanistPlaceholder", // placeholder loading animation
                )
            )

            bundle(
                /* alias = */ "googleWear",
                /* aliases = */ listOf(
                    "playServicesWearable",
                )
            )

            bundle(
                /* alias = */ "androidx",
                /* aliases = */ listOf(
                    "coreKtx",
                    "coreSplashscreen",
                    "collectionKtx",
                    "appcompat",
                    "window",
                    "activityKtx",
                    "activityCompose",
                    "fragmentKtx",
                    "paletteKtx",
                    "workRuntimeKtx",
                    "preferenceKtx",
                    // "slice-builders-ktx",
                    "startupRuntime",
                    "datastore",
                    "benchmarkJunit4",

                    "roomRuntime",
                    "roomKtx",
                    "roomPaging",

                    "lifecycleProcess",
                    "lifecycleRuntimeKtx",
                    "lifecycleRuntimeCompose",
                    "lifecycleViewmodelKtx",
                    "lifecycleViewmodelCompose",
                    "lifecycleViewmodelSavedstate",
                )
            )

            bundle(
                /* alias = */ "androidxApp",
                /* aliases = */ listOf(
                    "coreGoogleShortcuts",

                    "navigationRuntimeKtx",
                    "navigationUiKtx",
                    "navigationCompose",

                    "material",
                    "material3",
                    "material3WindowSize",

                    // compose
                    "composeMaterial",
                )
            )

            bundle(
                /* alias = */ "androidxWear",
                /* aliases = */ listOf(
                    "composeMaterialWear",
                    "composeNavigationWear"
                )
            )

            library(
                /* alias = */ "kotlinTest",
                /* group = */ "org.jetbrains.kotlin",
                /* artifact = */ "kotlin-test"
            ).versionRef("kotlin")

            library(
                /* alias = */ "kotlinxCoroutinesAndroid",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-coroutines-android"
            ).versionRef("kotlinxCoroutines")

            library(
                /* alias = */ "kotlinxCoroutinesPlayServices",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-coroutines-play-services"
            ).versionRef("kotlinxCoroutines")

            library(
                /* alias = */ "kotlinxCoroutinesTest",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-coroutines-test"
            ).versionRef("kotlinxCoroutines")

            library(
                /* alias = */ "kotlinxSerializationJson",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-serialization-json"
            ).versionRef("kotlinxSerialization")

            library(
                /* alias = */ "kotlinxSerializationProtobuf",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-serialization-protobuf"
            ).versionRef("kotlinxSerialization")

            library(
                /* alias = */ "kotlinxDatetime",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-datetime"
            ).version("0.4.0")

            library(
                /* alias = */ "kotlinxCollectionsImmutable",
                /* group = */ "org.jetbrains.kotlinx",
                /* artifact = */ "kotlinx-collections-immutable"
            ).version("0.3.5")

            library(
                /* alias = */ "ktorClientCore",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-client-core"
            ).versionRef("ktor")

            library(
                /* alias = */ "ktorClientAndroid",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-client-android"
            ).versionRef("ktor")

            library(
                /* alias = */ "ktorClientLogging",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-client-logging"
            ).versionRef("ktor")

            library(
                /* alias = */ "ktorClientContentNegotiation",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-client-content-negotiation"
            ).versionRef("ktor")

            library(
                /* alias = */ "ktorSerializationKotlinxJson",
                /* group = */ "io.ktor",
                /* artifact = */ "ktor-serialization-kotlinx-json"
            ).versionRef("ktor")

            library(
                /* alias = */ "playServicesWearable",
                /* group = */ "com.google.android.gms",
                /* artifact = */ "play-services-wearable"
            ).version("18.0.0")

            library(
                /* alias = */ "protobufKotlinLite",
                /* group = */ "com.google.protobuf",
                /* artifact = */ "protobuf-kotlin-lite"
            ).versionRef("protobuf")

            library(
                /* alias = */ "kspSymbolProcessingApi",
                /* group = */ "com.google.devtools.ksp",
                /* artifact = */ "symbol-processing-api"
            ).versionRef("ksp")

            library(
                "startupRuntime",
                "androidx.startup",
                "startup-runtime"
            ).version("1.1.1")

            library(
                /* alias = */ "datastore",
                /* group = */ "androidx.datastore",
                /* artifact = */ "datastore"
            ).version("1.1.0-alpha04")

            library(
                /* alias = */ "roomRuntime",
                /* group = */ "androidx.room",
                /* artifact = */ "room-runtime"
            ).versionRef("room")

            library(
                /* alias = */ "roomKtx",
                /* group = */ "androidx.room",
                /* artifact = */ "room-ktx"
            ).versionRef("room")

            library(
                /* alias = */ "roomPaging",
                /* group = */ "androidx.room",
                /* artifact = */ "room-paging"
            ).versionRef("room")

            library(
                /* alias = */ "roomCompiler",
                /* group = */ "androidx.room",
                /* artifact = */ "room-compiler"
            ).versionRef("room")

            library(
                /* alias = */ "window",
                /* group = */ "androidx.window",
                /* artifact = */ "window"
            ).version("1.1.0")

            library(
                /* alias = */ "appcompat",
                /* group = */ "androidx.appcompat",
                /* artifact = */ "appcompat"
            ).version("1.7.0-alpha03")

            library(
                /* alias = */ "coreKtx",
                /* group = */ "androidx.core",
                /* artifact = */ "core-ktx"
            ).version("1.12.0-rc01")

            library(
                /* alias = */ "coreSplashscreen",
                /* group = */ "androidx.core",
                /* artifact = */ "core-splashscreen"
            ).version("1.0.1")

            library(
                /* alias = */ "coreGoogleShortcuts",
                /* group = */ "androidx.core",
                /* artifact = */ "core-google-shortcuts"
            ).version("1.1.0")

            library(
                /* alias = */ "collectionKtx",
                /* group = */ "androidx.collection",
                /* artifact = */ "collection-ktx"
            ).version("1.3.0-beta01")

            library(
                /* alias = */ "activityKtx",
                /* group = */ "androidx.activity",
                /* artifact = */ "activity-ktx"
            ).version("1.7.2")

            library(
                /* alias = */ "fragmentKtx",
                /* group = */ "androidx.fragment",
                /* artifact = */ "fragment-ktx"
            ).version("1.6.0")

            library(
                /* alias = */ "activityCompose",
                /* group = */ "androidx.activity",
                /* artifact = */ "activity-compose"
            ).version("1.7.2")

            library(
                /* alias = */ "paletteKtx",
                /* group = */ "androidx.palette",
                /* artifact = */ "palette-ktx"
            ).version("1.0.0")

            library(
                /* alias = */ "workRuntimeKtx",
                /* group = */ "androidx.work",
                /* artifact = */ "work-runtime-ktx"
            ).version("2.8.1")

            library(
                /* alias = */ "preferenceKtx",
                /* group = */ "androidx.preference",
                /* artifact = */ "preference-ktx"
            ).version("1.2.1")

            library(
                /* alias = */ "sliceBuildersKtx",
                /* group = */ "androidx.slice",
                /* artifact = */ "slice-builders-ktx"
            ).version("1.0.0-alpha08")

            library(
                /* alias = */ "benchmarkJunit4",
                /* group = */ "androidx.benchmark",
                /* artifact = */ "benchmark-junit4"
            ).version("1.2.0-alpha15") // 1.2.0-beta03

            library(
                /* alias = */ "lifecycleProcess",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-process"
            ).versionRef("lifecycle")

            library(
                /* alias = */ "lifecycleRuntimeKtx",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-runtime-ktx"
            ).withoutVersion()

            library(
                /* alias = */ "lifecycleRuntimeCompose",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-runtime-compose"
            ).withoutVersion()

            library(
                /* alias = */ "lifecycleViewmodelKtx",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-viewmodel-ktx"
            ).versionRef("lifecycle")

            library(
                /* alias = */ "lifecycleViewmodelCompose",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-viewmodel-compose"
            ).versionRef("lifecycle")

            library(
                /* alias = */ "lifecycleViewmodelSavedstate",
                /* group = */ "androidx.lifecycle",
                /* artifact = */ "lifecycle-viewmodel-savedstate"
            ).versionRef("lifecycle")

            library(
                /* alias = */ "navigationRuntimeKtx",
                /* group = */ "androidx.navigation",
                /* artifact = */ "navigation-runtime-ktx"
            ).versionRef("navigation")

            library(
                /* alias = */ "hiltAndroid",
                /* group = */ "com.google.dagger",
                /* artifact = */ "hilt-android"
            ).versionRef("hilt")

            library(
                /* alias = */ "hiltAndroidCompiler",
                /* group = */ "com.google.dagger",
                /* artifact = */ "hilt-android-compiler"
            ).versionRef("hilt")

            library(
                /* alias = */ "hiltNavigationCompose",
                /* group = */ "androidx.hilt",
                /* artifact = */ "hilt-navigation-compose"
            ).version("1.1.0-alpha01")

            library(
                /* alias = */ "navigationUiKtx",
                /* group = */ "androidx.navigation",
                /* artifact = */ "navigation-ui-ktx"
            ).versionRef("navigation")

            library(
                /* alias = */ "navigationCompose",
                /* group = */ "androidx.navigation",
                /* artifact = */ "navigation-compose"
            ).versionRef("navigation")

            library(
                /* alias = */ "material",
                /* group = */ "com.google.android.material",
                /* artifact = */ "material"
            ).version("1.9.0-beta01")

            library(
                /* alias = */ "composeBom",
                /* group = */ "androidx.compose",
                /* artifact = */ "compose-bom"
            ).version("2023.08.00")

            library(
                /* alias = */ "material3",
                /* group = */ "androidx.compose.material3",
                /* artifact = */ "material3"
            ).withoutVersion()

            library(
                // cannot have `class` as alias postfix because gradle fails
                /* alias = */ "material3WindowSize",
                /* group = */ "androidx.compose.material3",
                /* artifact = */ "material3-window-size-class"
            ).withoutVersion()

            library(
                /* alias = */ "ui",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui"
            ).withoutVersion()

            library(
                /* alias = */ "uiViewbinding",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-viewbinding"
            ).withoutVersion()

            library(
                /* alias = */ "uiTooling",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-tooling"
            ).withoutVersion()

            library(
                /* alias = */ "uiToolingData",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-tooling-data"
            ).withoutVersion()

            library(
                /* alias = */ "uiToolingPreview",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-tooling-preview"
            ).withoutVersion()

            library(
                /* alias = */ "uiUtil",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-util"
            ).withoutVersion()

            library(
                /* alias = */ "composeMaterial",
                /* group = */ "androidx.compose.material",
                /* artifact = */ "material"
            ).withoutVersion()

            library(
                /* alias = */ "materialIconsCore",
                /* group = */ "androidx.compose.material",
                /* artifact = */ "material-icons-core"
            ).withoutVersion()

            library(
                /* alias = */ "materialIconsExtended",
                /* group = */ "androidx.compose.material",
                /* artifact = */ "material-icons-extended"
            ).withoutVersion()

            library(
                /* alias = */ "foundation",
                /* group = */ "androidx.compose.foundation",
                /* artifact = */ "foundation"
            ).withoutVersion()

            library(
                /* alias = */ "uiTextGoogleFonts",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-text-google-fonts"
            ).withoutVersion()

            bundle(
                "androidxCompose",
                listOf(
                    // compose
                    "ui",
                    "uiViewbinding",
                    "uiTooling",
                    "uiToolingData",
                    "uiToolingPreview",
                    "constraintlayout",
                    "runtimeTracing",
                    "metricsPerformance",
                    "uiUtil",
                    "composeFoundation",
                    "materialIconsCore",
                    "materialIconsExtended",
                    "foundation",
                    "uiTextGoogleFonts"
                )
            )

            library(
                /* alias = */ "runtimeTracing",
                /* group = */ "androidx.compose.runtime",
                /* artifact = */ "runtime-tracing"
            ).version("1.0.0-alpha03")

            library(
                /* alias = */ "uiTestJunit4",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-test-junit4"
            ).version("1.4.3")

            library(
                /* alias = */ "uiTestManifest",
                /* group = */ "androidx.compose.ui",
                /* artifact = */ "ui-test-manifest"
            ).withoutVersion()

            library(
                /* alias = */ "constraintlayout",
                /* group = */ "androidx.constraintlayout",
                /* artifact = */ "constraintlayout-compose"
            ).version("1.1.0-alpha10")

            library(
                /* alias = */ "metricsPerformance",
                /* group = */ "androidx.metrics",
                /* artifact = */ "metrics-performance"
            ).version("1.0.0-alpha04")

            library(
                /* alias = */ "composeFoundation",
                /* group = */ "androidx.wear.compose",
                /* artifact = */ "compose-foundation"
            ).versionRef("wearCompose")

            library(
                /* alias = */ "composeMaterialWear",
                /* group = */ "androidx.wear.compose",
                /* artifact = */ "compose-material"
            ).versionRef("wearCompose")

            library(
                /* alias = */ "composeNavigationWear",
                /* group = */ "androidx.wear.compose",
                /* artifact = */ "compose-navigation"
            ).versionRef("wearCompose")

            library(
                /* alias = */ "accompanistNavigationMaterial",
                /* group = */ "com.google.accompanist",
                /* artifact = */ "accompanist-navigation-material"
            ).versionRef("accompanist")

            library(
                /* alias = */ "accompanistAdaptive",
                /* group = */ "com.google.accompanist",
                /* artifact = */ "accompanist-adaptive"
            ).versionRef("accompanist")

            library(
                /* alias = */ "accompanistPlaceholder",
                /* group = */ "com.google.accompanist",
                /* artifact = */ "accompanist-placeholder"
            ).versionRef("accompanist")

            library(
                /* alias = */ "firebaseBom",
                /* group = */ "com.google.firebase",
                /* artifact = */ "firebase-bom"
            ).version("32.2.2")

            library(
                /* alias = */ "firebaseCrashlytics",
                /* group = */ "com.google.firebase",
                /* artifact = */ "firebase-crashlytics"
            ).withoutVersion()

            library(
                /* alias = */ "firebaseAnalyticsKtx",
                /* group = */ "com.google.firebase",
                /* artifact = */ "firebase-analytics-ktx"
            ).withoutVersion()

            library(
                /* alias = */ "firebasePerfKtx",
                /* group = */ "com.google.firebase",
                /* artifact = */ "firebase-perf-ktx"
            ).withoutVersion()

            bundle(
                "firebase",
                listOf(
                    "firebaseCrashlytics",
                    "firebaseAnalyticsKtx",
                    "firebasePerfKtx"
                )
            )

            library(
                /* alias = */ "slf4jSimple",
                /* group = */ "org.slf4j",
                /* artifact = */ "slf4j-simple"
            ).version("2.0.7")

            library(
                /* alias = */ "junit",
                /* group = */ "junit",
                /* artifact = */ "junit"
            ).version("4.13.2")


            library(
                /* alias = */ "junitExt",
                /* group = */ "androidx.test.ext",
                /* artifact = */ "junit"
            ).version("1.1.5")

            library(
                /* alias = */ "espressoCore",
                /* group = */ "androidx.test.espresso",
                /* artifact = */ "espresso-core"
            ).version("3.5.1")

            library(
                /* alias = */ "desugarJdkLibs",
                /* group = */ "com.android.tools",
                /* artifact = */ "desugar_jdk_libs"
            ).version("2.0.3")
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
