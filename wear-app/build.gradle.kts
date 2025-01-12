plugins {
    id("buildsrc.convention.kotlin-multiplatform-mobile")
}
dependencies {
    implementation(libs.bundles.androidxWear)
    implementation(libs.bundles.googleWear)
    implementation(project(":common:view"))
}
val appId = "com.monoid.hackernews.wear"
compose {
    resources {
        packageOfResClass = appId
    }
}
android {
    namespace = appId
    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "sn13p9hDhpAU"
            keyAlias = "release"
            keyPassword = "h8G8xDZYuceM"
        }
    }
    defaultConfig {
        applicationId = appId
        versionCode = 1
        versionName = "1.0"
        // reduces apk sizes by not including unsupported languages
        androidResources {
            @Suppress("UnstableApiUsage")
            localeFilters += listOf("en", "es")
        }
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
