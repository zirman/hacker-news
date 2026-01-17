plugins {
    id("kotlin-multiplatform-library")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("androidx.room")
}
val packageNamespace = "com.monoid.hackernews.common.data"
kotlin {
    android {
        namespace = packageNamespace
        @Suppress("UnstableApiUsage")
        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.files.add(File("proguard-rules.pro"))
        }
    }
    sourceSets {
        jvmMain.dependencies {
            implementation(libs.bundles.jvmMain)
        }
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
    kspIosArm64(libs.roomCompiler)
    kspIosSimulatorArm64(libs.roomCompiler)
    // kspIosX64(libs.roomCompiler)
}
room {
    schemaDirectory("$projectDir/schemas")
}
