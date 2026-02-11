plugins {
    alias(libs.plugins.hoopsnow.android.library)
    alias(libs.plugins.hoopsnow.android.hilt)
}

android {
    namespace = "com.hoopsnow.nba.core.testing"
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)
    api(projects.core.data)

    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
    api(libs.hilt.android.testing)
}
