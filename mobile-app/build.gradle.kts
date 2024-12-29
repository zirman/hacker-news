@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("kmpapplication")
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.googlePlayServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.firebasePerf)
    id("hackernews.detekt")
}
kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.bundles.androidx)
            implementation(libs.bundles.androidxCompose)
            implementation(libs.bundles.androidxApp)
            implementation(libs.koinAndroid)
            implementation(compose.preview)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.uiTooling)
            implementation(libs.ktorClientAndroid)
            implementation(libs.material3Adaptive)
            implementation(libs.material3AdaptiveLayout)
            implementation(libs.collectionKtx)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(libs.bundles.googleApp)
            implementation(libs.bundles.google)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        commonMain.dependencies {
            api(libs.annotation)
            compileOnly(libs.koinCore)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.uiTest)
            implementation(compose.uiUtil)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project(":common:injection"))
            implementation(project(":common:view"))
//            implementation(libs.activityCompose)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.kotlinx)
            implementation(libs.navigationCompose)
            implementation(libs.koinAnnotations)
            implementation(libs.lifecycleProcess)
            implementation(libs.slf4jSimple)
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
    }
}
val packageNamespace = "com.monoid.hackernews"
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
android {
    namespace = packageNamespace
    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "sn13p9hDhpAU"
            keyAlias = "release"
            keyPassword = "h8G8xDZYuceM"
        }
    }
    defaultConfig {
        applicationId = "com.monoid.hackernews"
        versionCode = 44
        versionName = "1.1.5"
        // reduces apk sizes by not including unsupported languages
        resourceConfigurations += setOf("en", "es")
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}
