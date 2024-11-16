package co.imprint.sdksample

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import co.imprint.imprintsdk.application.Imprint
import co.imprint.sdksample.ui.theme.ImprintSDKTheme
import co.imprint.imprintsdk.application.ImprintConfiguration

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ImprintSDKTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  Text(
    text = "Hello $name!",
    modifier = modifier.clickable {
        onStartApplication(context = context)
    }
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ImprintSDKTheme {
    Greeting("Android")
  }
}

private fun onStartApplication(context: Context) {
  val configuration = ImprintConfiguration(
    sessionToken = "GENERATE_IN_POST_AUTH",
    environment = ImprintConfiguration.Environment.STAGING
  ).apply {
    var externalReferenceId = "YOUR_CUSTOMER_ID"
    var applicationId = "IMPRINT_GENERATED_GUID"
    var additionalData = mapOf("other" to "value")

    var onCompletion = { state: ImprintConfiguration.CompletionState, metadata: Map<String, String> ->
      when (state) {
        ImprintConfiguration.CompletionState.OFFER_ACCEPTED -> {
          Log.d("Test","Offer accepted\\n${metadata.toString()}")
        }
        ImprintConfiguration.CompletionState.REJECTED -> {
          Log.d("Test", "Application rejected\n${metadata.toString()}")
        }
        ImprintConfiguration.CompletionState.ABANDONED -> {
          Log.d("Test", "Application abandoned\n${metadata.toString()}")
        }
      }
    }
  }

  Imprint.startApplication(context, configuration)
}