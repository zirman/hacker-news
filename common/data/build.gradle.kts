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
        extraWarnings.set(true)
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    androidTarget { }
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.koinAndroid)
            implementation(libs.roomKtx)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinxCoroutinesSwing)
        }
        commonMain.dependencies {
            compileOnly(libs.koinCore)
            project.dependencies.coreLibraryDesugaring(libs.desugarJdkLibsNio)
            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(libs.annotation)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.ktor)
            implementation(libs.collectionKtx)
            implementation(libs.ktorClientJava)
            implementation(libs.roomRuntime)
            implementation(libs.sqliteBundled)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(project(":common:injection"))
            api(libs.koinAnnotations)
        }
        commonTest.dependencies {
            implementation(libs.bundles.test)
        }
    }
    sourceSets.named("commonMain") {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}
dependencies {
    add("kspCommonMainMetadata", libs.koinKspCompiler)
    add("kspAndroid", libs.koinKspCompiler)
    add("kspJvm", libs.koinKspCompiler)
}
// Trigger Common Metadata Generation from Native tasks
project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
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
//    ksp(libs.roomCompiler)
    add("kspAndroid", libs.roomCompiler)
    add("kspJvm", libs.roomCompiler)
}
room {
    schemaDirectory("$projectDir/schemas")
}
ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}
