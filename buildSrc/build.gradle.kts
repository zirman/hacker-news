plugins {
    `kotlin-dsl`
}
kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}
dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.bundles.plugins)
}
tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}
