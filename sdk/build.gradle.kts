plugins {
  id("com.android.library")
  id("kotlin-parcelize")
  id("maven-publish")
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.compose.compiler)
}

android {
  namespace = "co.imprint.sdk"
  compileSdk = 35

  defaultConfig {
    minSdk = 26
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    compose = true
  }

  buildTypes {
    release {
      isMinifyEnabled = false
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
      groupId = "co.imprint.sdk"
      artifactId = "imprint-sdk"
      version = "0.2.0"

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
  implementation(libs.androidx.ui)
  implementation(libs.material3)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.runtime.livedata)

  // Lifecycle dependency for view models in Compose
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.activity.compose)

  //Koin
  implementation(libs.koin.android)

  // Optional: For testing Compose UI
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  testImplementation(libs.json)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.androidx.core.testing)
  testImplementation(libs.mockk)
  testImplementation (libs.koin.test.junit4)
}