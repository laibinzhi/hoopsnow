plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.hoopsnow.android.hilt)
}

android {
    namespace = "com.hoopsnow.nba.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
