@file:OptIn(DelicateMetroGradleApi::class, ExperimentalRoborazziApi::class)

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import dev.zacsweers.metro.gradle.DelicateMetroGradleApi

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("dev.zacsweers.metro")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("io.github.takahirom.roborazzi")
    id("detekt-rules")
}
dependencies {
    implementation(project(":core"))
    implementation(project(":view"))
    testImplementation(libs.bundles.commonTest)
    testImplementation(libs.composeUiTest)

    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    ksp(project(":injection-processor"))
    // https://github.com/google/ksp/issues/2595
    ksp(project(":screenshot-processor"))
    lintChecks(libs.composeLintChecks)
    debugImplementation(libs.uiTestManifest)
}
val appId = "com.monoid.hackernews"
val appName = "Hacker News"
android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
    buildToolsVersion = libs.versions.buildToolsVersion.get()
    namespace = appId
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        applicationId = appId
        versionCode = 49
        versionName = "2.0.0"
        // reduces apk sizes by not including unsupported languages
        androidResources {
            @Suppress("UnstableApiUsage")
            localeFilters += listOf("en", "es")
        }
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility =
            JavaVersion.toVersion(libs.versions.jvmTarget.get().toInt())
        targetCompatibility =
            JavaVersion.toVersion(libs.versions.jvmTarget.get().toInt())
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
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
            //            resValue("string", "app_name", "$appName Debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            //            resValue("string", "app_name", appName)
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                project.file("proguard-rules.pro"),
            )
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        warningsAsErrors = true
        baseline = file("lint-baseline.xml")
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}
compose {
    resources {
        publicResClass = true
        generateResClass = always
        packageOfResClass = appId
    }
}
metro {
    enableTopLevelFunctionInjection = true
    generateContributionHintsInFir = true
    enableKotlinVersionCompatibilityChecks = false
}
roborazzi {
    outputDir.set(file("src/test/screenshotTest"))
    // Directory for comparison images (Experimental option)
    compare {
        outputDir.set(file("build/roborazzi/comparison"))
    }
}
