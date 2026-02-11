plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.hoopsnow.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hoopsnow.nba.core.network"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp.logging)
}
