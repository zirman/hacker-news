@file:Suppress("OPT_IN_USAGE")

package buildsrc.convention

import com.google.devtools.ksp.gradle.KspAATask
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("io.github.takahirom.roborazzi")
    id("buildsrc.convention.detekt-rules")
}
val libs = the<LibrariesForLibs>()
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project.dependencies.platform(libs.koinBom))
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
            compileOnly(libs.koinCore)
            api(libs.bundles.commonMain)
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        androidMain.dependencies {
            api(project.dependencies.platform(libs.kotilnCoroutinesBom))
            api(compose.uiTooling)
            api(compose.preview)
            api(compose.components.uiToolingPreview)
            api(libs.bundles.androidMain)
        }
        jvmMain.dependencies {
            api(project.dependencies.platform(libs.kotilnCoroutinesBom))
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
    }
    jvm()
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    compilerOptions {
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
}
val kspAndroid by configurations.named("kspAndroid")
val kspJvm by configurations.named("kspJvm")
val kspIosX64 by configurations.named("kspIosX64")
val kspIosArm64 by configurations.named("kspIosArm64")
val kspIosSimulatorArm64 by configurations.named("kspIosSimulatorArm64")
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    kspCommonMainMetadata(libs.koinKspCompiler)
    kspAndroid(libs.koinKspCompiler)
    kspJvm(libs.koinKspCompiler)
    kspIosX64(libs.koinKspCompiler)
    kspIosArm64(libs.koinKspCompiler)
    kspIosSimulatorArm64(libs.koinKspCompiler)
    kspAndroid(project(":ksp-processors:screenshot"))
    lintChecks(libs.composeLintChecks)
    debugImplementation(libs.uiTestManifest)
}
ksp {
    arg("KOIN_CONFIG_CHECK", "false")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
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
// Trigger Common Metadata Generation from Native tasks
tasks.withType<KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
tasks.withType<KspAATask>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
