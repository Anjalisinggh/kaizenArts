pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*") // For Android-related plugins
                includeGroupByRegex("com\\.google.*")  // For Google-related plugins
                includeGroupByRegex("androidx.*")     // For androidx plugins
            }
        }
        mavenCentral()  // For common dependencies
        maven("https://jitpack.io")  // For dependencies hosted on JitPack
        gradlePluginPortal()  // For Gradle plugins
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // Disallows project-level repositories
    repositories {
        google()  // Android-related dependencies
        mavenCentral()  // Common dependencies
        maven("https://jitpack.io")  // JitPack dependencies
    }
}

rootProject.name = "KaizenArts"  // Set the project name

// Include your modules here
include(":app")  // Add more modules if needed, e.g., include(":moduleName")
