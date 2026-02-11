pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HoopsNow"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")

// Core modules
include(":core:common")
include(":core:model")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:network")
include(":core:designsystem")
include(":core:ui")
include(":core:testing")

// Feature modules
include(":feature:games:api")
include(":feature:games:impl")
include(":feature:teams:api")
include(":feature:teams:impl")
include(":feature:players:api")
include(":feature:players:impl")
include(":feature:favorites:api")
include(":feature:favorites:impl")
