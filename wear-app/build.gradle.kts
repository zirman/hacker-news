plugins {
    id("buildsrc.convention.kotlin-multiplatform-mobile")
}
dependencies {
    implementation(libs.bundles.androidxWear)
    implementation(libs.bundles.googleWear)
    implementation(project(":common:view"))
}
android {
    namespace = "com.monoid.hackernews.wear"
    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "sn13p9hDhpAU"
            keyAlias = "release"
            keyPassword = "h8G8xDZYuceM"
        }
    }
    defaultConfig {
        applicationId = "com.monoid.hackernews.wear"
        versionCode = 1
        versionName = "1.0"
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
}
