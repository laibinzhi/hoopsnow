plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.hoopsnow.android.hilt)
}

android {
    namespace = "com.hoopsnow.nba.core.datastore"
}

dependencies {
    api(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}
