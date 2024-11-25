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

  fun startApplication(context: Context) {
    // Configure the Imprint SDK with the required parameters
    val configuration = ImprintConfiguration(
      token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjdXN0b21lcl9pZCI6IkJPT0tJTkciLCJleHAiOjE3MzEwNDkyMTksImlhdCI6MTczMTA0NTYxOX0.7va3IWy5ky1EWJ7-USOMfNdJS85tfO1gwe--OwJl4gI",
      environment = ImprintConfiguration.Environment.STAGING,
      externalReferenceId = "YOUR_CUSTOMER_ID",
      applicationId = "IMPRINT_GENERATED_GUID",
      additionalData = mapOf("other" to "value"),
    )

    // Callback function triggered when the application process is completed
    val onCompletion =
      { state: ImprintConfiguration.CompletionState, metadata: Map<String, String>? ->
        val metadataInfo = metadata?.toString() ?: "No metadata"
        val resultText = when (state) {
          ImprintConfiguration.CompletionState.OFFER_ACCEPTED -> {
            "Offer accepted\n$metadataInfo"
          }
          ImprintConfiguration.CompletionState.REJECTED -> {
            "Application rejected\n$metadataInfo"
          }
          ImprintConfiguration.CompletionState.ABANDONED -> {
            "Application abandoned"
          }
        }
        _statusText.value = resultText
      }

    // Start the application process with the provided context, configuration, and callback
    Imprint.startApplication(
      context = context,
      configuration = configuration,
      onCompletion = onCompletion,
    )
  }
}