pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral() } }
rootProject.name = "BSV"
include(":app", ":core-ui", ":core-settings", ":feature-home", ":feature-profile", ":feature-visuals", ":feature-interface")
