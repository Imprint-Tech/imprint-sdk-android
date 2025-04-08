package co.imprint.sdk.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import co.imprint.sdk.di.IsolatedKoinComponent
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class ApplicationActivity : ComponentActivity(), IsolatedKoinComponent {

  private val viewModel: ApplicationViewModel by viewModel()

  companion object {
    const val APPLICATION_CONFIGURATION = "config"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      MaterialTheme {
        ApplicationView(viewModel)
      }
    }
  }
}