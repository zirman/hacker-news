import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
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
            implementation(libs.koinLoggerSlf4j)
            implementation(libs.koinKtor)
            implementation(libs.ktorClientAndroid)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(libs.bundles.firebase)
            implementation(libs.kotlinxCoroutinesAndroid)
        }
        jvmMain.dependencies {
            implementation(libs.ktorClientJava)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        commonMain.dependencies {
            compileOnly(libs.koinCore)

            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            api(libs.koinAnnotations)
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.firebaseBom))
            implementation(libs.kermit)
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
    namespace = "com.monoid.hackernews.common.injection"
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
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
