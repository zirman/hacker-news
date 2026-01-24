plugins {
    id("kotlin-android-app")
}
dependencies {
    implementation(project(":view"))
    testImplementation(libs.bundles.commonTest)
    testImplementation(libs.composeUiTest)
}
android {
    val appId = "com.monoid.hackernews"
    namespace = appId
    defaultConfig {
        applicationId = appId
        versionCode = 49
        versionName = "2.0.0"
    }
    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "sn13p9hDhpAU"
            keyAlias = "release"
            keyPassword = "h8G8xDZYuceM"
        }
    }
    buildTypes {
        release {
            // isDebuggable = true
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
