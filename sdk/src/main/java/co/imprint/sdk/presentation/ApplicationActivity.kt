package co.imprint.sdk.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import co.imprint.sdk.di.IsolatedKoinComponent
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class ApplicationActivity : ComponentActivity(), IsolatedKoinComponent {

  companion object {
    const val APPLICATION_CONFIGURATION = "config"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val viewModel: ApplicationViewModel by viewModel()
    setContent {
      MaterialTheme {
        ApplicationView(viewModel)
      }
    }
  }
}