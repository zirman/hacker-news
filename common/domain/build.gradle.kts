import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
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
            implementation(libs.ktorClientAndroid)
            implementation(libs.collectionKtx)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
        }
        jvmMain.dependencies {
            implementation(libs.ktorClientJava)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.koinBom))
            compileOnly(libs.koinCore)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            implementation(libs.navigationCompose)
            implementation(libs.annotation)
            api(libs.koinAnnotations)
            api(project(":common:injection"))
            api(project(":common:data"))
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
        sourceSets.named("commonMain") {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}
android {
    namespace = "com.monoid.hackernews.common.domain"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    buildTypes {
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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
ksp {
    arg("KOIN_CONFIG_CHECK", "false")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}
// Trigger Common Metadata Generation from Native tasks
project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
