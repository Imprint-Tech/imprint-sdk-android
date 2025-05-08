import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
  id("com.android.library")
  id("kotlin-parcelize")
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.maven.publish)
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
}

val isCi = System.getenv("CI") == "true"
val sdkVersion = project.findProperty("SDK_VERSION") as String? ?: "0.0.1-Local"

mavenPublishing {
  configure(AndroidSingleVariantLibrary(
    // the published variant
    variant = "release",
    // whether to publish a sources jar
    sourcesJar = true,
    // whether to publish a javadoc jar
    publishJavadocJar = true,
  ))

  publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
  if (!isCi) {
    signAllPublications()
  }

  coordinates("co.imprint.sdk", "imprint-sdk", sdkVersion)

  pom {
    name.set("Imprint Android SDK")
    description.set("Embed the Imprint application experience in your Android APP.")
    inceptionYear.set("2024")
    url.set("https://github.com/Imprint-Tech/imprint-sdk-android")
    licenses {
      license {
        name.set("MIT License")
        url.set("https://opensource.org/licenses/MIT")
        distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
      }
    }
    developers {
      developer {
        id.set("Imprint-Tech")
        name.set("Imprint-Tech")
        url.set("https://github.com/Imprint-Tech/")
      }
    }
    scm {
      url.set("https://github.com/Imprint-Tech/imprint-sdk-android")
      connection.set("scm:git:git://github.com/Imprint-Tech/imprint-sdk-android.git")
      developerConnection.set("scm:git:ssh://git@github.com/Imprint-Tech/imprint-sdk-android.git")
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