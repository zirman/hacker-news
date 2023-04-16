plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.monoid.hackernews.util"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))

    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.material3)
    implementation(libs.bundles.google)

    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
}
