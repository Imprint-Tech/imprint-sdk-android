package co.imprint.sdksample

import android.content.Context
import androidx.lifecycle.ViewModel
import co.imprint.imprintsdk.application.Imprint
import co.imprint.imprintsdk.application.ImprintConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

  private val _statusText = MutableStateFlow("")
  val statusText: StateFlow<String> = _statusText

  // Function to start the application
  fun startApplication(context: Context) {
    val configuration = ImprintConfiguration(
      sessionToken = "GENERATE_IN_POST_AUTH",
      environment = ImprintConfiguration.Environment.STAGING
    ).apply {
      externalReferenceId = "YOUR_CUSTOMER_ID"
      applicationId = "IMPRINT_GENERATED_GUID"
      additionalData = mapOf("other" to "value")
      onCompletion = { state: ImprintConfiguration.CompletionState, metadata: Map<String, String>? ->
        val metadataInfo = metadata?.toString() ?: "No metadata"
        val resultText = when (state) {
          ImprintConfiguration.CompletionState.OFFER_ACCEPTED -> {
            "Offer accepted\n$metadataInfo"
          }
          ImprintConfiguration.CompletionState.REJECTED -> {
            "Application rejected\n$metadataInfo"
          }
          ImprintConfiguration.CompletionState.ABANDONED -> {
            "Application abandoned\n$metadataInfo"
          }
        }

        // Update the status text in a lifecycle-safe way
        _statusText.value = resultText
      }
    }

    Imprint.startApplication(context, configuration)
  }
}