plugins {
    `kotlin-dsl`
}
kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}
dependencies {
    implementation(plugin(libs.plugins.kotlinMultiplatform))
    implementation(plugin(libs.plugins.androidApplication))
    implementation(plugin(libs.plugins.androidMultiplatformLibrary))
    implementation(plugin(libs.plugins.kotlinSerialization))
    implementation(plugin(libs.plugins.compose))
    implementation(plugin(libs.plugins.composeCompiler))
    implementation(plugin(libs.plugins.ksp))
    implementation(plugin(libs.plugins.metro))
    implementation(plugin(libs.plugins.room))
    implementation(plugin(libs.plugins.googlePlayServices))
    implementation(plugin(libs.plugins.crashlytics))
    implementation(plugin(libs.plugins.firebasePerf))
    implementation(plugin(libs.plugins.detektGradle))
    implementation(plugin(libs.plugins.roborazzi))
}
tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>): Provider<String> =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
