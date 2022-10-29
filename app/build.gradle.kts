plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.monoid.hackernews"
    compileSdk = 33
    buildToolsVersion = "33.0.0"

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
        targetSdk = 33
        versionCode = 22
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"

        freeCompilerArgs = listOf(
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.ui.text.ExperimentalTextApi",
            "-opt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.app)
    implementation(libs.bundles.google)
    implementation(libs.bundles.google.app)
    compileOnly(libs.slf4j.simple)
    ksp(libs.room.compiler)
}
