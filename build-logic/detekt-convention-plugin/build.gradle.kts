plugins {
    `kotlin-dsl`
}
group = "com.monoid.hackernews.buildlogic"
kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}
dependencies {
    compileOnly(libs.detektGradlePlugin)
}
tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}
gradlePlugin {
    plugins {
        register("detekt") {
            id = "hackernews.detekt"
            implementationClass = "DetektConventionPlugin"
        }
    }
}
