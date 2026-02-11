plugins {
    alias(libs.plugins.hoopsnow.android.feature)
    alias(libs.plugins.hoopsnow.android.library.compose)
    alias(libs.plugins.hoopsnow.android.hilt)
}

android {
    namespace = "com.hoopsnow.nba.feature.teams.impl"
}

dependencies {
    implementation(projects.feature.teams.api)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
}
