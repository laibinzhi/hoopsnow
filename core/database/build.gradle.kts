plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.hoopsnow.android.hilt)
    alias(libs.plugins.hoopsnow.android.room)
}

android {
    namespace = "com.hoopsnow.nba.core.database"
}

dependencies {
    api(projects.core.model)
    implementation(libs.kotlinx.coroutines.android)
}
