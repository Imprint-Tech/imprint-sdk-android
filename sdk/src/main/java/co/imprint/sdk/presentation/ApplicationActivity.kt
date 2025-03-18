package co.imprint.sdk.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import co.imprint.sdk.di.dispatchersModule
import co.imprint.sdk.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

internal class ApplicationActivity : ComponentActivity() {

  companion object {
    const val APPLICATION_CONFIGURATION = "config"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    startKoin {
      // Log Koin into Android logger
      androidLogger()
      // Reference Android context
      androidContext(applicationContext)
      // Load modules
      modules(dispatchersModule, viewModelModule)
    }

    val viewModel: ApplicationViewModel by viewModel()
    setContent {
      MaterialTheme {
        ApplicationView(viewModel)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    stopKoin()
  }
}
