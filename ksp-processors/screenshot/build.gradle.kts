plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.kspSymbolProcessingApi)
    api(libs.kotlinPoet)
    api(libs.kotlinPoetKsp)
    implementation(kotlin("reflect"))
}
