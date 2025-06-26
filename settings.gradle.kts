pluginManagement {
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
// In settings.gradle.kts

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // --- MAPBOX REPOSITORY CONFIGURATION ---
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                password = "sk.eyJ1Ijoid2hvc2FoaWxndXB0YSIsImEiOiJjbWNkczdlOTEwa2ljMnNzYms4bHFsMmIwIn0.3WDU5tMbsLRLccRzOSghvw"
            }
        }
    }
}

rootProject.name = "icbmpFinalBoss"
include(":automotive")
 