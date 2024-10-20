@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.googlePlayServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.firebasePerf)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    id("hackernews.detekt")
}
kotlin {
    compilerOptions {
        extraWarnings.set(true)
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    androidTarget { }
    sourceSets {
        androidMain.dependencies {
            project.dependencies.coreLibraryDesugaring(libs.desugarJdkLibsNio)
            compileOnly(libs.koinCore)
            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.preview)
            implementation(compose.ui)
            implementation(compose.uiTest)
            implementation(compose.uiTooling)
            implementation(compose.uiUtil)
//            implementation(libs.activityCompose)
            implementation(libs.annotation)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.androidx)
            implementation(libs.bundles.androidxCompose)
            implementation(libs.bundles.androidxApp)
            implementation(libs.bundles.google)
            implementation(libs.bundles.googleApp)
            implementation(libs.collectionKtx)
            implementation(libs.navigationCompose)
            implementation(libs.koinAndroid)
            implementation(libs.lifecycleProcess)
            implementation(libs.slf4jSimple)
            implementation(libs.bundles.ktor)

            implementation(libs.material3Adaptive)
            implementation(libs.material3AdaptiveLayout)

            // lintChecks(libs.composeLintChecks)
            // debugImplementation(libs.uiTestManifest)
        }
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project(":common:injection"))
            implementation(project(":common:view"))
        }
        commonTest.dependencies {
            implementation(libs.bundles.test)
        }
    }
}
compose.resources {
    publicResClass = true
    packageOfResClass = "com.monoid.hackernews"
    generateResClass = always
}
android {
    namespace = "com.monoid.hackernews"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
    buildToolsVersion = libs.versions.buildToolsVersion.get()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
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
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        targetSdkPreview = libs.versions.targetSdk.get()
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
            applicationIdSuffix = ".debug"
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
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
    }
    packaging {
        resources {
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}
