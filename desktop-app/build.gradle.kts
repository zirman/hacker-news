import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("buildsrc.convention.kotlin-multiplatform-desktop")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":common:view"))
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
            mainClass = "com.monoid.hackernews.Main_desktopKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = name
                packageVersion = "1.0.0"
                buildTypes.release.proguard {
                    version = "7.6.1"
                    isEnabled = true
                    optimize = true
                    obfuscate = false // Currently obfuscated builds crash
                    configurationFiles.from("rules.pro")
                }
            }
        }
    }
}
