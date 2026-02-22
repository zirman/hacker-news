plugins {
    id("kotlin-multiplatform-library")
}
val packageNamespace = "com.monoid.hackernews.common.view"
kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    android {
        namespace = packageNamespace
        androidResources.enable = true
    }
    sourceSets {
        commonMain.dependencies {
            api(project(":domain"))
        }
    }
}
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
