package co.imprint.imprintsdk.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme


internal class ApplicationActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Retrieve configuration and make it non-nullable
    val configuration = intent.getParcelableExtra<ImprintConfiguration>("config")
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