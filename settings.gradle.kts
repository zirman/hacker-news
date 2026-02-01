@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/kt/dev/")
    }
}
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/kt/dev/")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "hacker-news"
include(":android-app")
include(":desktop-app")
include(":wear-app")
includeSubdir(":core", "common")
includeSubdir(":injection", "common")
includeSubdir(":data", "common")
includeSubdir(":domain", "common")
includeSubdir(":view", "common")
includeSubdir(":android", "common")
includeSubdir(":wear", "common")
includeSubdir(":injection-processor", "ksp")
includeSubdir(":screenshot-processor", "ksp")
private fun includeSubdir(name: String, directory: String) {
    include(name)
    project(name).projectDir = file("$directory/${name.removePrefix(":")}")
}
