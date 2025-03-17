package co.imprint.sdk.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ApplicationActivity : ComponentActivity() {

  companion object {
    const val APPLICATION_CONFIGURATION = "config"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val viewModel: ApplicationViewModel by viewModels()
    setContent {
      MaterialTheme {
        ApplicationView(viewModel)
      }
    }
  }
}
