package co.imprint.sdk.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.SavedStateHandle
import co.imprint.sdk.di.IsolatedKoinComponent
import co.imprint.sdk.domain.repository.ImageRepository
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

internal class ApplicationActivity : ComponentActivity(), IsolatedKoinComponent {

  companion object {
    const val APPLICATION_CONFIGURATION = "config"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val stateMap = mutableMapOf<String, Any?>()
    intent?.extras?.let { bundle ->
      for (key in bundle.keySet()) {
        stateMap[key] = bundle.get(key)
      }
    }
    savedInstanceState?.let { bundle ->
      for (key in bundle.keySet()) {
        stateMap[key] = bundle.get(key)
      }
    }

    // Create SavedStateHandle with the map
    val state = SavedStateHandle(stateMap)

    // Get the ImageRepository from your isolated Koin context
    val imageRepository = getKoin().get<ImageRepository>()

    // Provide both required parameters to the ViewModel
    val viewModel: ApplicationViewModel by viewModel {
      parametersOf(state, imageRepository)
    }

    setContent {
      MaterialTheme {
        ApplicationView(viewModel)
      }
    }
  }
}