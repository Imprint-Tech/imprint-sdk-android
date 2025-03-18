package co.imprint.sdk.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import co.imprint.sdk.di.dispatchersModule
import co.imprint.sdk.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

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

    val viewModel: ApplicationViewModel by viewModels()
    setContent {
      MaterialTheme {
        ApplicationView(viewModel)
      }
    }
  }
}
