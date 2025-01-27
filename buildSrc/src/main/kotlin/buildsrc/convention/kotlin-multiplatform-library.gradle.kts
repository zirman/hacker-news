package buildsrc.convention

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
            implementation(project(":common:core"))
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
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
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    kspCommonMainMetadata(libs.koinKspCompiler)
    "kspAndroid"(libs.koinKspCompiler)
    "kspJvm"(libs.koinKspCompiler)
    "kspIosX64"(libs.koinKspCompiler)
    "kspIosArm64"(libs.koinKspCompiler)
    "kspIosSimulatorArm64"(libs.koinKspCompiler)
    "kspAndroid"(project(":ksp-processors:screenshot"))
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
