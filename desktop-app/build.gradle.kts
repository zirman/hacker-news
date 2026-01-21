import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("kotlin-multiplatform-desktop")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":view"))
        }
    }
}
val name = "com.monoid.hackernews"
compose {
    resources {
        packageOfResClass = name
    }
    desktop {
        application {
            mainClass = "com.monoid.hackernews.common.view.Main_jvmKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = name
                packageVersion = "1.0.0"
            }
            buildTypes.release.proguard {
                version = "7.8.1"
                isEnabled = true
                optimize = true
                obfuscate = true
                configurationFiles.from(
                    project.file("proguard-rules.pro"),
                )
            }
        }
    }
}
