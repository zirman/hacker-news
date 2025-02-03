plugins {
    kotlin("jvm")
}

dependencies {
    api(libs.kspSymbolProcessingApi)
    api(libs.kotlinPoet)
    implementation(kotlin("reflect"))
}
