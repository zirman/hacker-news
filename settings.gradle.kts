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
