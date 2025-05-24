plugins {
    id("buildsrc.convention.kotlin-multiplatform-mobile")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":common:view")) {
                // work around duplicate Koin JVM dependencies
                exclude("io.insert-koin", "koin-core-annotations-jvm")
            }
        }
    }
}
val appId = "com.monoid.hackernews"
val appName = "Hacker News"
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
        versionCode = 46
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
    buildTypes {
        debug {
            resValue("string", "app_name", "$appName Debug")
        }
        release {
            resValue("string", "app_name", appName)
            signingConfig = signingConfigs.getByName("release")
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
