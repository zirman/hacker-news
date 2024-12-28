@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    id("hackernews.detekt")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    androidTarget {
    }
    jvm {
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.bundles.androidxCompose)
            implementation(libs.ktorClientAndroid)
            implementation(libs.collectionKtx)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(compose.preview)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.uiTooling)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinxCoroutinesSwing)
            implementation(libs.ktorClientJava)
            implementation(compose.desktop.common)
            implementation(compose.desktop.components.animatedImage)
            implementation(compose.desktop.components.splitPane)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        commonMain.dependencies {
            api(project(":common:domain"))
            compileOnly(libs.koinCore)
            implementation(compose.animation)
            implementation(compose.animationGraphics)
            implementation(compose.components.resources)

            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)

            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(libs.annotation)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            api(libs.koinAnnotations)
            implementation(libs.bundles.ktor)
            implementation(libs.jetbrainsLifecycleRuntimeCompose)
            implementation(libs.jetbrainsLifecycleViewmodel)
            implementation(libs.jetbrainsLifecycleViewmodelCompose)
            implementation(libs.navigationCompose)
            implementation(libs.material3WindowSizeClassMultiplatform)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
        sourceSets.named("commonMain") {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    add("kspCommonMainMetadata", libs.koinKspCompiler)
    add("kspAndroid", libs.koinKspCompiler)
    add("kspJvm", libs.koinKspCompiler)
    add("kspIosX64", libs.koinKspCompiler)
    add("kspIosArm64", libs.koinKspCompiler)
    add("kspIosSimulatorArm64", libs.koinKspCompiler)
}
compose.resources {
    publicResClass = true
    packageOfResClass = "com.monoid.hackernews.common.view"
    generateResClass = always
}
android {
    namespace = "com.monoid.hackernews.common.view"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    buildTypes {
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
    }
}
ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}
// Trigger Common Metadata Generation from Native tasks
project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
