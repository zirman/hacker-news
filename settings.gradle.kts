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
include(":mobile-app")
include(":desktop-app")
include(":wear-app")
includeSubdir("common", "core")
includeSubdir("common", "injection")
includeSubdir("common", "data")
includeSubdir("common", "domain")
includeSubdir("common", "view")
includeSubdir("ksp-processors", "injection")
includeSubdir("ksp-processors", "screenshot")
private fun includeSubdir(directory: String, name: String) {
    val path = ":${directory}-${name}"
    include(path)
    project(path).projectDir = file("$directory/$name")
}
