package buildsrc.convention

import com.android.build.gradle.tasks.asJavaVersion
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("buildsrc.convention.detekt-rules")
}
val libs = the<LibrariesForLibs>()
kotlin {
    sourceSets {
        commonMain.dependencies {
            // compose
            implementation(compose.animation)
            implementation(compose.animationGraphics)
            implementation(compose.components.resources)
            //implementation(compose.components.uiToolingPreview)
            //implementation(compose.desktop.common)
            //implementation(compose.desktop.components.animatedImage)
            //implementation(compose.desktop.components.splitPane)
            //implementation(compose.desktop.currentOs)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            //implementation(compose.material3AdaptiveNavigationSuite)
            //implementation(compose.preview)
            implementation(compose.runtime)
            implementation(compose.ui)
            //implementation(compose.uiTooling)
            implementation(compose.uiUtil)
            // koin
            implementation(project.dependencies.platform(libs.koinBom))
            compileOnly(libs.koinCore)
            api(libs.koinAnnotations)
            implementation(libs.koinCompose)
            implementation(libs.koinComposeViewmodel)

            implementation(libs.bundles.ktor)

            implementation(libs.annotation)

            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlin)
            implementation(libs.ktorSerializationKotlinJson)
            implementation(libs.sqliteBundled)
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))

            implementation(libs.roomRuntime)

            implementation(project.dependencies.platform(libs.firebaseBom))
            implementation(libs.kermit)

            implementation(libs.jetbrainsLifecycleRuntimeCompose)
            implementation(libs.jetbrainsLifecycleViewmodel)
            implementation(libs.jetbrainsLifecycleViewmodelCompose)
            implementation(libs.jetbrainsNavigationCompose)
            implementation(libs.jetbrainsCore)
            implementation(libs.jetbrainsWindowCore)
            implementation(libs.jetbrainsSavedState)
            implementation(libs.material3WindowSizeClassMultiplatform)

            // implementation(libs.activityCompose)
            implementation(libs.lifecycleProcess)
            implementation(libs.slf4jSimple)
            implementation(project(":common:injection"))
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.kotilnCoroutinesBom))
            implementation(libs.roomKtx)
            implementation(libs.kotlinCoroutinesAndroid)
            implementation(libs.collectionKtx)
            implementation(libs.koinAndroid)
            implementation(libs.koinKtor)
            implementation(libs.koinLoggerSlf4j)
            implementation(libs.ktorClientAndroid)
            implementation(libs.bundles.firebase)
            implementation(libs.material3AdaptiveNavigation)
            implementation(libs.material3AdaptiveNavigationSuite)
            implementation(libs.metricsPerformance)

            implementation(libs.bundles.androidx)
            implementation(libs.bundles.googleApp)
            implementation(libs.bundles.google)
            implementation(libs.material3Adaptive)
            implementation(libs.material3AdaptiveLayout)
        }
        iosMain.dependencies {
            implementation(libs.koinCore)
            implementation(libs.ktorClientDarwin)
        }
    }
    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
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
        targetSdk = libs.versions.targetSdk.get().toInt()
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility =
            JavaLanguageVersion.of(libs.versions.jvmTarget.get()).asJavaVersion()
        targetCompatibility =
            JavaLanguageVersion.of(libs.versions.jvmTarget.get()).asJavaVersion()
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
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
    }
}
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    kspCommonMainMetadata(libs.koinKspCompiler)
    "kspAndroid"(libs.koinKspCompiler)
    "kspIosX64"(libs.koinKspCompiler)
    "kspIosArm64"(libs.koinKspCompiler)
    "kspIosSimulatorArm64"(libs.koinKspCompiler)
    lintChecks(libs.composeLintChecks)
}
ksp {
    arg("KOIN_CONFIG_CHECK", "true")
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
