plugins {
    alias(libs.plugins.hoopsnow.android.application)
    alias(libs.plugins.hoopsnow.android.application.compose)
    alias(libs.plugins.hoopsnow.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hoopsnow.nba"

    defaultConfig {
        applicationId = "com.hoopsnow.nba"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)

    // Feature modules - api for navigation keys, impl for screens
    implementation(projects.feature.games.api)
    implementation(projects.feature.games.impl)
    implementation(projects.feature.teams.api)
    implementation(projects.feature.teams.impl)
    implementation(projects.feature.players.api)
    implementation(projects.feature.players.impl)
    implementation(projects.feature.favorites.api)
    implementation(projects.feature.favorites.impl)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.hilt.navigation.compose)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
