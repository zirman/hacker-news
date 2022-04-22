import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.protobuf")
}
android {
    compileSdk = 32
    buildToolsVersion = "33.0.0-rc2"
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
        minSdk = 21
        targetSdk = 32
        versionCode = 4
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
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=kotlinx.coroutines.FlowPreview",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.InternalCoroutinesApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-Xopt-in=androidx.compose.ui.text.ExperimentalTextApi",
            "-Xopt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
        )
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0-alpha08"
    }
}
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("java") {
                    option("lite")
                }
            }
            task.builtins {
                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1")

    implementation("androidx.core:core-ktx:1.9.0-alpha02")

    implementation("androidx.appcompat:appcompat:1.4.1")

    implementation("androidx.benchmark:benchmark-junit4:1.0.0")

    implementation("androidx.collection:collection-ktx:1.2.0")

    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.activity:activity-compose:1.4.0")

    implementation("androidx.fragment:fragment-ktx:1.4.1")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.4.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")

    implementation("androidx.compose.ui:ui:1.2.0-alpha08")
    implementation("androidx.compose.ui:ui-viewbinding:1.2.0-alpha08")

    implementation("androidx.compose.material:material:1.2.0-alpha08")

    implementation("androidx.compose.material3:material3:1.0.0-alpha09")
    implementation("androidx.compose.material:material-icons-core:1.2.0-alpha08")
    implementation("androidx.compose.material:material-icons-extended:1.2.0-alpha08")

    implementation("androidx.compose.ui:ui-tooling:1.2.0-alpha08")
    implementation("androidx.compose.ui:ui-tooling-preview:1.2.0-alpha08")
    implementation("androidx.compose.ui:ui-util:1.2.0-alpha08")

    implementation("androidx.compose.foundation:foundation:1.2.0-alpha08")

    implementation("androidx.compose.runtime:runtime-livedata:1.2.0-alpha08")

    implementation("androidx.datastore:datastore:1.0.0")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0")

    implementation("androidx.window:window:1.0.0")

    implementation("androidx.navigation:navigation-runtime-ktx:2.4.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.2")
    implementation("androidx.navigation:navigation-compose:2.4.2")

    implementation("androidx.room:room-runtime:2.4.2")
    kapt("androidx.room:room-compiler:2.4.2")
    implementation("androidx.room:room-ktx:2.4.2")
    implementation("androidx.room:room-paging:2.4.2")

    implementation("com.google.android.material:material:1.5.0")

    implementation("com.google.protobuf:protobuf-kotlin-lite:3.20.1")

    implementation("com.google.accompanist:accompanist-navigation-animation:0.24.7-alpha")
    implementation("com.google.accompanist:accompanist-navigation-material:0.24.7-alpha")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.7-alpha")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.24.7-alpha")

    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-android:2.0.0")
    implementation("io.ktor:ktor-client-logging:2.0.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0")
}
