import org.gradle.kotlin.dsl.resources

plugins {
    id("buildsrc.convention.kotlin-multiplatform-library")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("androidx.room")
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
