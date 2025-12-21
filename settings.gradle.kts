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
include(":common-core")
project(":common-core").projectDir = file("common/core")
include(":common-injection")
project(":common-injection").projectDir = file("common/injection")
include(":common-data")
project(":common-data").projectDir = file("common/data")
include(":common-domain")
project(":common-domain").projectDir = file("common/domain")
include(":common-view")
project(":common-view").projectDir = file("common/view")
include(":ksp-processors-injection")
project(":ksp-processors-injection").projectDir = file("ksp-processors/injection")
include(":ksp-processors-screenshot")
project(":ksp-processors-screenshot").projectDir = file("ksp-processors/screenshot")
