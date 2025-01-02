package buildsrc.convention

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.compose.ExperimentalComposeLibrary

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
    jvm("desktop") {
    }
    sourceSets {
        val desktopMain by getting
        commonMain.dependencies {
            implementation(compose.animation)
            implementation(compose.animationGraphics)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.desktop.common)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.desktop.components.animatedImage)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.desktop.components.splitPane)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.preview)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.uiTooling)
            implementation(compose.uiUtil)
            implementation(libs.jetbrainsLifecycleViewmodel)
            implementation(libs.jetbrainsLifecycleViewmodelCompose)
            implementation(libs.jetbrainsLifecycleRuntimeCompose)
            implementation(libs.bundles.datastore)
            implementation(project.dependencies.platform(libs.koinBom))
            compileOnly(libs.koinCore)
            api(libs.koinAnnotations)
            implementation(libs.koinCompose)
            implementation(libs.koinComposeViewmodel)
            implementation(libs.bundles.kotlin)
            implementation(libs.bundles.ktor)
            implementation(libs.slf4jSimple)

            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.kotilnCoroutinesBom))
            implementation(project(":common:injection"))
            implementation(project(":common:view"))
        }
        desktopMain.dependencies {
            implementation(project.dependencies.platform(libs.kotilnCoroutinesBom))
            implementation(compose.desktop.currentOs)
            implementation(libs.koinAnnotations)
            implementation(libs.kotlinCoroutinesSwing)
            implementation(libs.ktorClientJava)
            implementation(libs.ktorSerializationKotlinJson)
        }
        sourceSets {
            commonMain {
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }
        }
    }
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
