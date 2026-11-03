pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositories { google(); mavenCentral() } }
rootProject.name = "smart-documents-platform"
include(":shared:core", ":androidApp", ":desktopApp")
