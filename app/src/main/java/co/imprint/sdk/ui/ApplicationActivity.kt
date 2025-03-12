package co.imprint.sdk.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import co.imprint.sdk.viewmodel.ApplicationViewModel
import co.imprint.sdk.api.ImprintConfiguration


internal class ApplicationActivity : ComponentActivity() {

  companion object {
    const val APPLICATION_CONFIGURATION = "config"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Retrieve configuration and make it non-nullable
    val configuration = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      intent.getParcelableExtra(APPLICATION_CONFIGURATION, ImprintConfiguration::class.java)
    } else {
      @Suppress("DEPRECATION")
      intent.getParcelableExtra<ImprintConfiguration>(APPLICATION_CONFIGURATION)
    }

    configuration?.let {
      val viewModel = ApplicationViewModel(configuration = configuration)
      // Set the Compose UI content
      setContent {
        MaterialTheme {
          ApplicationView(viewModel)
        }
      }
    }
  }
}