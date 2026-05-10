@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kotlin-multiplatform-library")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("androidx.room3")
}
val packageNamespace = "com.monoid.hackernews.common.data"
kotlin {
    android {
        namespace = packageNamespace
        @Suppress("UnstableApiUsage")
        optimization {
            consumerKeepRules.apply {
                publish = true
                files.add(File("proguard-rules.pro"))
            }
        }
    }
    sourceSets {
        webMain.dependencies {
            implementation(libs.sqliteWeb)
            implementation(npm("sql-js-worker", layout.projectDirectory.file("worker").asFile))
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
    kspJs(libs.roomCompiler)
    kspWasmJs(libs.roomCompiler)
    kspIosArm64(libs.roomCompiler)
    kspIosSimulatorArm64(libs.roomCompiler)
    // kspIosX64(libs.roomCompiler)
}
room3 {
    schemaDirectory("$projectDir/schemas")
}
