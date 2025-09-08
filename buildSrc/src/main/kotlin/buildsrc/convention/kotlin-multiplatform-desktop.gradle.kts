package buildsrc.convention

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("org.jetbrains.compose.hot-reload")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("dev.zacsweers.metro")
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
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
    jvm("desktop")
    compilerOptions {
        // Should be able to remove in 2.2.20-Beta2
        // https://issuetracker.google.com/issues/429988549
        // apiVersion = KOTLIN_2_1
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}
compose {
    resources {
        publicResClass = true
        generateResClass = always
    }
}
