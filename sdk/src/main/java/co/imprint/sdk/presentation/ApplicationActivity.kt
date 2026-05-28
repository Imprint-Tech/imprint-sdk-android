package co.imprint.sdk.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.imprint.sdk.presentation.theme.ImprintTheme
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.imprint.sdk.di.SdkServiceLocator
import kotlinx.coroutines.launch

internal class ApplicationActivity : ComponentActivity() {

  private val viewModel: ApplicationViewModel by lazy {
    val factory = viewModelFactory {
      initializer {
        SdkServiceLocator.createViewModel(createSavedStateHandle())
      }
    }
    ViewModelProvider(this, factory)[ApplicationViewModel::class.java]
  }

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