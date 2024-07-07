plugins {
    id("java-library")
    id("kotlin")
    id("hackernews.detekt")
}

dependencies {
    detektPlugins(libs.detektRulesRuleauthors)
    implementation(libs.detektApi)
    implementation(libs.detektTooling)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
