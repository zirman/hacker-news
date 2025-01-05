package buildsrc.convention

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("buildsrc.convention.detekt-rules")
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}
val libs = the<LibrariesForLibs>()
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project.dependencies.platform(libs.koinBom))
            compileOnly(libs.koinCore)
            api(libs.koinAnnotations)
            api(libs.koinCompose)
            api(libs.koinComposeViewmodel)

            api(compose.animation)
            api(compose.animationGraphics)
            api(compose.components.resources)
            //api(compose.components.uiToolingPreview)
            api(compose.foundation)
            api(compose.material3)
            api(compose.materialIconsExtended)
            //api(compose.preview)
            api(compose.runtime)
            api(compose.ui)
            //api(compose.uiTooling)
            api(compose.uiUtil)

            api(libs.annotation)
            api(libs.bundles.kotlin)
            api(libs.bundles.ktor)
            api(libs.roomRuntime)
            api(libs.bundles.datastore)
            api(libs.sqliteBundled)
            api(libs.ktorSerializationKotlinJson)
            api(project.dependencies.platform(libs.kotlinWrappersBom))
            api(project.dependencies.platform(libs.firebaseBom))
            api(libs.jetbrainsLifecycleRuntimeCompose)
            api(libs.jetbrainsLifecycleViewmodel)
            api(libs.jetbrainsLifecycleViewmodelCompose)
            api(libs.jetbrainsNavigationCompose)
            api(libs.jetbrainsCore)
            api(libs.jetbrainsWindowCore)
            api(libs.jetbrainsSavedState)
            api(libs.material3WindowSizeClassMultiplatform)
            api(libs.slf4jSimple)
            api(libs.kermit)
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
        androidMain.dependencies {
            api(libs.roomKtx)
            api(project.dependencies.platform(libs.kotilnCoroutinesBom))
            api(libs.kotlinCoroutinesAndroid)
            api(libs.collectionKtx)
            api(libs.koinAndroid)
            api(libs.koinKtor)
            api(libs.koinLoggerSlf4j)
            api(libs.ktorClientAndroid)
            api(libs.bundles.firebase)
            api(libs.material3AdaptiveNavigation)
            api(libs.material3AdaptiveNavigationSuite)
            api(libs.lifecycleProcess)
            api(libs.metricsPerformance)
            api(libs.bundles.androidx)
            api(libs.bundles.googleApp)
            api(libs.bundles.google)
            api(libs.material3Adaptive)
            api(libs.material3AdaptiveLayout)
        }
        jvmMain.dependencies {
            api(project.dependencies.platform(libs.kotilnCoroutinesBom))
            api(compose.desktop.common)
            api(compose.desktop.currentOs)
            @OptIn(ExperimentalComposeLibrary::class)
            api(compose.desktop.components.animatedImage)
            @OptIn(ExperimentalComposeLibrary::class)
            api(compose.desktop.components.splitPane)
            api(libs.kotlinCoroutinesSwing)
            api(libs.ktorClientJava)
        }
        iosMain.dependencies {
            api(libs.koinCore)
            api(libs.ktorClientDarwin)
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
    lintChecks(libs.composeLintChecks)
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
// Trigger Common Metadata Generation from Native tasks
tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
