@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
    }
}
pluginManagement {
    buildscript {
        repositories {
            mavenCentral()
            maven {
                url = uri("https://storage.googleapis.com/r8-releases/raw")
            }
        }
        dependencies {
            classpath("com.android.tools:r8:8.13.0-dev")
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver") version "1.0.0"
}
toolchainManagement {
    jvm {
        javaRepositories {
            repository("foojay") {
                resolverClass.set(org.gradle.toolchains.foojay.FoojayToolchainResolver::class.java)
            }
        }
    }
}
rootProject.name = "HackerNews"
include(":mobile-app")
include(":desktop-app")
include(":wear-app")
include(":common:core")
include(":common:view")
include(":common:domain")
include(":common:data")
include(":detekt-rules")
include(":ksp-processors:screenshot")
