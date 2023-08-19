@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.googlePlayServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.firebasePerf)
}

android {
    namespace = "com.monoid.hackernews"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

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
        minSdk = 26
        targetSdk = 34
        versionCode = 40
        versionName = "1.1.1"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"

        freeCompilerArgs = listOf(
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.ui.text.ExperimentalTextApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
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

kapt {
    correctErrorTypes = true
}

dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibs)

    implementation(project(":common:injection"))
    implementation(project(":common:view"))
    implementation(project(":common:util"))

    implementation(platform(libs.composeBom))
    implementation(platform(libs.firebaseBom))

    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidxCompose)
    implementation(libs.bundles.androidxApp)
    implementation(libs.bundles.google)
    implementation(libs.bundles.googleApp)
    implementation(libs.bundles.firebase)
    implementation(libs.slf4jSimple)

    implementation(libs.hiltAndroid)
    kapt(libs.hiltAndroidCompiler)

    implementation(libs.hiltNavigationCompose)

    androidTestImplementation(libs.uiTestJunit4)
    debugImplementation(libs.uiTestManifest)
}
