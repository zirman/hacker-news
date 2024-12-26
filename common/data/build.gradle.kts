import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.room)
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
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            //linkerOpts.add("-lsqlite3")
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.koinAndroid)
            implementation(libs.roomKtx)
            implementation(libs.kotlinxCoroutinesAndroid)
            implementation(libs.bundles.ktor)
            implementation(libs.ktorClientAndroid)
            implementation(libs.collectionKtx)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
        }
        jvmMain.dependencies {
            implementation(libs.kotlinxCoroutinesSwing)
            implementation(libs.bundles.ktor)
            implementation(libs.ktorClientJava)
        }
        iosMain.dependencies {
        }
        commonMain.dependencies {
            compileOnly(libs.koinCore)
            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(libs.annotation)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.kotlinx)
            implementation(libs.ktorSerializationKotlinxJson)
            implementation(libs.bundles.ktor)
            implementation(libs.roomRuntime)
            implementation(libs.sqliteBundled)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project(":common:injection"))
            api(libs.koinAnnotations)
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
    }
    sourceSets.named("commonMain") {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}
android {
    namespace = "com.monoid.hackernews.common.data"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    buildTypes { }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    // room
    add("kspAndroid", libs.roomCompiler)
    add("kspJvm", libs.roomCompiler)
    add("kspIosX64", libs.roomCompiler)
    add("kspIosArm64", libs.roomCompiler)
    add("kspIosSimulatorArm64", libs.roomCompiler)
    // koin
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
room {
    schemaDirectory("$projectDir/schemas")
}
// Trigger Common Metadata Generation from Native tasks
project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
