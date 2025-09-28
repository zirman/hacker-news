@file:Suppress("OPT_IN_USAGE")

package buildsrc.convention

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("org.jetbrains.compose.hot-reload")
    id("com.google.devtools.ksp")
    id("dev.zacsweers.metro")
    id("io.github.takahirom.roborazzi")
    id("buildsrc.convention.detekt-rules")
}
val libs = the<LibrariesForLibs>()
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project.dependencies.platform(libs.kotlinWrappersBom))
            api(project.dependencies.platform(libs.firebaseBom))
            api(compose.animation)
            api(compose.animationGraphics)
            api(compose.components.resources)
            api(compose.foundation)
            api(compose.material3)
            api(compose.materialIconsExtended)
            api(compose.runtime)
            api(compose.ui)
            api(compose.uiUtil)
            api(libs.bundles.commonMain)
        }
        androidMain.dependencies {
            api(project.dependencies.platform(libs.kotlinCoroutinesBom))
            api(compose.uiTooling)
            api(compose.preview)
            api(compose.components.uiToolingPreview)
            api(libs.bundles.androidMain)
        }
        jvmMain.dependencies {
            api(project.dependencies.platform(libs.kotlinCoroutinesBom))
            api(compose.desktop.common)
            api(compose.desktop.currentOs)
            @OptIn(ExperimentalComposeLibrary::class)
            api(compose.desktop.components.animatedImage)
            @OptIn(ExperimentalComposeLibrary::class)
            api(compose.desktop.components.splitPane)
            api(libs.bundles.jvmMain)
        }
        iosMain.dependencies {
            api(libs.bundles.iosMain)
        }
        commonTest.dependencies {
            implementation(libs.bundles.commonTest)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        androidUnitTest {
            kotlin.srcDir("build/generated/ksp/android/androidDebug/screenshotTest")
        }
        androidUnitTest.dependencies {
            implementation(libs.bundles.androidUnitTest)
        }
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
    jvm()
    androidTarget()
//    iosX64()
    iosArm64()
    iosSimulatorArm64()
    compilerOptions {
        // Should be able to remove in 2.2.20-Beta2
        // https://issuetracker.google.com/issues/429988549
        // apiVersion = KOTLIN_2_1
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}
android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
    buildToolsVersion = libs.versions.buildToolsVersion.get()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility =
            JavaVersion.toVersion(libs.versions.jvmTarget.get().toInt())
        targetCompatibility =
            JavaVersion.toVersion(libs.versions.jvmTarget.get().toInt())
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    sourceSets.named("main").get().apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
        resources.srcDirs("src/commonMain/resources")
    }
    packaging {
        resources {
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        warningsAsErrors = true
        baseline = file("lint-baseline.xml")
    }
}
val kspAndroid by configurations.named("kspAndroid")
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    // https://github.com/google/ksp/issues/2595
    lintChecks(libs.composeLintChecks)
    debugImplementation(libs.uiTestManifest)
}
compose {
    resources {
        publicResClass = true
        generateResClass = always
    }
}
roborazzi {
    outputDir.set(file("src/androidUnitTest/screenshotTest"))
    // Directory for comparison images (Experimental option)
    compare {
        outputDir.set(file("build/roborazzi/comparison"))
    }
}
