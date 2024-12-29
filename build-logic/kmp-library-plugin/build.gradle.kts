plugins {
    `kotlin-dsl`
}
group = "com.monoid.hackernews.buildlogic"
kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}
dependencies {
    compileOnly(libs.kotlinMultiplatformPlugin)
    compileOnly(libs.androidApplicationPlugin)
    compileOnly(libs.composeCompilerPlugin)
    compileOnly(libs.kspPlugin)
    compileOnly(libs.composePlugin)
}
tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}
gradlePlugin {
    plugins {
        register("kmplibrary") {
            id = "kmplibrary"
            implementationClass = "KmpLibraryPlugin"
        }
    }
}
