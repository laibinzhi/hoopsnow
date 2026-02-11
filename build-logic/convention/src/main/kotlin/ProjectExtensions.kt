import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun CommonExtension<*, *, *, *, *, *>.configureKotlinAndroid(project: Project) {
    compileSdk = project.libs.findVersion("compileSdk").get().toString().toInt()

    defaultConfig {
        minSdk = project.libs.findVersion("minSdk").get().toString().toInt()
    }

    compileOptions {
        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17
        targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
    }

    project.configureKotlin()
}

internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

internal fun CommonExtension<*, *, *, *, *, *>.configureAndroidCompose(project: Project) {
    buildFeatures {
        compose = true
    }

    project.dependencies {
        val bom = project.libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))
        add("implementation", project.libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        add("debugImplementation", project.libs.findLibrary("androidx-compose-ui-tooling").get())
    }
}
