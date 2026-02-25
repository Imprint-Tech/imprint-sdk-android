package co.imprint.sdk.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.imprint.sdk.presentation.theme.ImprintTheme
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import co.imprint.sdk.di.IsolatedKoinComponent
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class ApplicationActivity : ComponentActivity(), IsolatedKoinComponent {

  private val viewModel: ApplicationViewModel by viewModel()

  companion object {
    const val APPLICATION_CONFIGURATION = "config"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ImprintTheme {
        ApplicationView(viewModel)
      }
    }

    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        viewModel.onDismiss()
      }
    })

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.navigationEvents.collect { event ->
          when (event) {
            is NavigationEvent.Finish -> {
              finish()
            }
          }
        }
      }
    }
  }
}