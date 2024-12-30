import org.gradle.kotlin.dsl.resources

plugins {
    id("kmplibrary")
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.room)
    id("hackernews.detekt")
}
kotlin {
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
}
dependencies {
    // room
    "kspAndroid"(libs.roomCompiler)
    "kspJvm"(libs.roomCompiler)
    "kspIosX64"(libs.roomCompiler)
    "kspIosArm64"(libs.roomCompiler)
    "kspIosSimulatorArm64"(libs.roomCompiler)
}
val packageNamespace = "com.monoid.hackernews.common.data"
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
android {
    namespace = packageNamespace
}
room {
    schemaDirectory("$projectDir/schemas")
}
