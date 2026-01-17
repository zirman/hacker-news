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
        androidMain.dependencies {
            implementation(libs.lifecycleViewmodelNavigation3)
        }
        commonMain.dependencies {
            api(project(":domain"))
        }
        jvmMain.dependencies {
            api(libs.lifecycleViewmodelNavigation3)
        }
    }
}
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
