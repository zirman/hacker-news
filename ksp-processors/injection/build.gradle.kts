plugins {
    kotlin("jvm")
}
dependencies {
    compileOnly(libs.kspSymbolProcessingApi)
    compileOnly(libs.kotlinPoet)
    compileOnly(libs.kotlinPoetKsp)
    implementation(libs.android)
    implementation(libs.metroRuntimeJvm)
    implementation(project(":common-injection"))
}
