package buildsrc.convention

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("buildsrc.convention.detekt-rules")
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}
val libs = the<LibrariesForLibs>()
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":common:core"))
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
    jvm("desktop")
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}
dependencies {
    kspCommonMainMetadata(libs.koinKspCompiler)
    "kspDesktop"(libs.koinKspCompiler)
}
compose {
    resources {
        publicResClass = true
        generateResClass = always
    }
}
ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}