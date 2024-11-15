// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "Demo"
include(":app")
include(":lib")
