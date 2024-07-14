plugins {
    id("java-library")
    id("kotlin")
    id("hackernews.detekt")
}

dependencies {
    detektPlugins(libs.detektRulesRuleauthors)
    compileOnly(libs.detektApi)
    compileOnly(libs.detektTooling)
    testImplementation(libs.detektTest)
    testImplementation(libs.junitJupiter)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
