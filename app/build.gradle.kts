plugins {
    alias(libs.plugins.hoopsnow.android.application)
    alias(libs.plugins.hoopsnow.android.application.compose)
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
    implementation(projects.shared)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
