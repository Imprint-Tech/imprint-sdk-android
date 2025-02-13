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
      client_secret = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjdXN0b21lcl9pZCI6IjEyMyIsInByb2R1Y3RVVUlEIjoiUFJEVC12MS1hMGVmMzNlOS01OTc2LTRlNTYtYWVkMS0yZDBkN2NmOTQwZDUiLCJleHAiOjE3MzEwNDkyMTksImlhdCI6MTczMTA0NTYxOX0.6W35ZsUTK1qXirOUymqlyLC8jtM5uoaeb9ZagbxrmXY",
      environment = ImprintConfiguration.Environment.SANDBOX,
      application_id = "IMPRINT_GENERATED_GUID",
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
          ImprintConfiguration.CompletionState.ERROR -> {
            "Error occurred"
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