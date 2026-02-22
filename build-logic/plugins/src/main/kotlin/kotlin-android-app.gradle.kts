@file:Suppress("OPT_IN_USAGE")
@file:OptIn(DelicateMetroGradleApi::class)

import dev.zacsweers.metro.gradle.DelicateMetroGradleApi

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("dev.zacsweers.metro")
    id("io.github.takahirom.roborazzi")
    id("detekt-rules")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}
val libs: VersionCatalog = the<VersionCatalogsExtension>().named("libs")
android {
    compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()
    compileSdkPreview = libs.findVersion("compileSdkPreview").get().requiredVersion
    buildToolsVersion = libs.findVersion("buildToolsVersion").get().requiredVersion
    defaultConfig {
        minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
        targetSdk = libs.findVersion("targetSdk").get().requiredVersion.toInt()
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
            JavaVersion.toVersion(libs.findVersion("jvmTarget").get().requiredVersion.toInt())
        targetCompatibility =
            JavaVersion.toVersion(libs.findVersion("jvmTarget").get().requiredVersion.toInt())
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                project.file("proguard-rules.pro"),
            )
        }
    }
    lint {
        warningsAsErrors = true
        baseline = file("lint-baseline.xml")
    }
}
dependencies {
    implementation(project(":core"))
    coreLibraryDesugaring(libs.findLibrary("desugarJdkLibsNio").get())
    ksp(project(":injection-processor"))
    ksp(project(":screenshot-processor"))
    lintChecks(libs.findLibrary("composeLintChecks").get())
    debugImplementation(libs.findLibrary("uiTestManifest").get())
}
compose {
    resources {
        publicResClass = true
        generateResClass = always
    }
}
metro {
    enableTopLevelFunctionInjection = true
    generateContributionHintsInFir = true
    enableKotlinVersionCompatibilityChecks = false
}
roborazzi {
    // outputDir.set(file("src/commonTest/screenshotTest"))
    // Directory for comparison images (Experimental option)
    compare {
        outputDir.set(file("build/roborazzi/comparison"))
    }
}
