plugins {
    id("kotlin")
    id("hackernews.detekt")
}
dependencies {
    compileOnly(libs.detektApi)
    compileOnly(libs.detektTooling)
    detektPlugins(libs.detektRulesRuleauthors)
    testImplementation(libs.assertjCore)
    testImplementation(libs.detektTest)
    testImplementation(libs.junitJupiter)
}
tasks.named<Test>("test") {
    useJUnitPlatform()
}
