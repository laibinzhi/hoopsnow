plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.hoopsnow.android.library.compose)
}

android {
    namespace = "com.hoopsnow.nba.core.designsystem"
}

dependencies {
    api(projects.core.model)

    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.icons.extended)
    api(libs.androidx.compose.foundation)
    api(libs.coil.compose)

    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
