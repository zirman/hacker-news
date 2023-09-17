plugins {
    id(libs.plugins.androidLibrary.get().pluginId)
    alias(libs.plugins.kotlinAndroid)
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

android {
    namespace = "com.monoid.hackernews.util"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug { }
        release { }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibs)

    implementation(platform(libs.composeBom))
    implementation(platform(libs.firebaseBom))

    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidxCompose)
    implementation(libs.material3)
    implementation(libs.bundles.google)

    androidTestImplementation(libs.uiTestJunit4)
    debugImplementation(libs.uiTestManifest)
}
