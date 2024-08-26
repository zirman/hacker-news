plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    id("hackernews.detekt")
}
kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    androidTarget { }
    jvm { }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.koinAndroid)
        }
        jvmMain.dependencies { }
        commonMain.dependencies {
            compileOnly(libs.koinCore)
            project.dependencies.coreLibraryDesugaring(libs.desugarJdkLibsNio)
            implementation(libs.annotation)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.ktor)
            implementation(libs.collectionKtx)
            implementation(libs.ktorClientJava)
            implementation(libs.roomRuntime)
            implementation(libs.roomKtx)
            implementation(libs.sqliteBundled)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(project(":common:injection"))
        }
        commonTest.dependencies {
            implementation(libs.bundles.test)
        }
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
