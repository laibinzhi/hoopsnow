plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hoopsnow.nba.feature.favorites.api"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation3.runtime)
}
