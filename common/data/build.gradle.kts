plugins {
    id("kotlin-multiplatform-library")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("androidx.room")
}
val packageNamespace = "com.monoid.hackernews.common.data"
kotlin {
    androidLibrary {
        namespace = packageNamespace
    }
}
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
dependencies {
    // room
    // https://github.com/google/ksp/issues/2595
    kspAndroid(libs.roomCompiler)
    kspJvm(libs.roomCompiler)
//    kspIosX64(libs.roomCompiler)
    kspIosArm64(libs.roomCompiler)
    kspIosSimulatorArm64(libs.roomCompiler)
}
room {
    schemaDirectory("$projectDir/schemas")
}
