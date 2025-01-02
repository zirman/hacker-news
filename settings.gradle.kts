@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HackerNews"
include(":mobile-app")
include(":desktop-app")
include(":wear-app")
include(":common:injection")
include(":common:view")
include(":common:domain")
include(":common:data")
include(":detekt-rules")
