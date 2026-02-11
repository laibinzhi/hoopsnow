plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hoopsnow.nba.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
