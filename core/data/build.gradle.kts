plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.hoopsnow.android.hilt)
}

android {
    namespace = "com.hoopsnow.nba.core.data"
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.network)

    implementation(libs.kotlinx.coroutines.android)
}
