@file:Suppress("OPT_IN_USAGE")

package buildsrc.convention

import com.android.build.api.dsl.androidLibrary
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
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
            implementation(project(":common:core"))
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
    androidLibrary {
        compileSdk = libs.versions.compileSdk.get().toInt()
        compileSdkPreview = libs.versions.compileSdkPreview.get()
        minSdk = libs.versions.minSdk.get().toInt()
        buildToolsVersion = libs.versions.buildToolsVersion.get()
        enableCoreLibraryDesugaring = true
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
        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
            }
        }
    }
    jvm()
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
val kspAndroid by configurations.named("kspAndroid")
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    kspAndroid(project(":ksp-processors:injection"))
    // https://github.com/google/ksp/issues/2595
    kspAndroid(project(":ksp-processors:screenshot"))
    lintChecks(libs.composeLintChecks)
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
