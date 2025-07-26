plugins {
    kotlin("jvm")
    id("buildsrc.convention.detekt-rules")
}
dependencies {
    compileOnly(libs.detektApi)
    compileOnly(libs.detektTooling)
    detektPlugins(libs.detektRulesRuleauthors)
    testImplementation(libs.assertjCore)
    testImplementation(libs.detektTest)
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformEngine)
    testRuntimeOnly(libs.junitPlatformLauncher)
}
tasks.named<Test>("test") {
    useJUnitPlatform()
}
