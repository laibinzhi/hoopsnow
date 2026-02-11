plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.hoopsnow.android.library.compose)
}

android {
    namespace = "com.hoopsnow.nba.core.ui"
}

dependencies {
    api(projects.core.model)
    api(projects.core.designsystem)

    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
