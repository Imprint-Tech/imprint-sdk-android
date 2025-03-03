plugins {
  id("com.android.library")
  id("kotlin-parcelize")
  id("maven-publish")
  alias(libs.plugins.jetbrains.kotlin.android)
}

android {
  namespace = "co.imprint.imprintsdk"
  compileSdk = 34

  defaultConfig {
    minSdk = 26
    targetSdk = 34

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    compose = true
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }

  publishing {
    singleVariant("release") {
      withSourcesJar()
    }
  }
}

publishing {
  publications {
    create<MavenPublication>("release") {
      groupId = "com.github.Imprint"
      artifactId = "imprint-sdk-android"
      version = "0.1.8"

      afterEvaluate {
        from(components["release"])
      }
    }
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)

  // Compose core dependencies
  implementation(libs.ui)
  implementation(libs.material3)
  implementation(libs.ui.tooling.preview)
  implementation(libs.androidx.runtime.livedata)

  // Lifecycle dependency for view models in Compose
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  implementation(libs.androidx.activity.compose)

  // Optional: For testing Compose UI
  androidTestImplementation(libs.ui.test.junit4)
  debugImplementation(libs.ui.tooling)
}