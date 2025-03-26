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
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven {
      url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
    mavenLocal()
    maven {
      url = uri("https://jitpack.io")
    }
  }
}

rootProject.name = "Imprint SDK"
include(":sdk")
include(":sdk-demo")
