package co.imprint.imprintsdk.application

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ApplicationViewModel(private val configuration: ImprintConfiguration) : ViewModel() {
  private val host = when (configuration.environment) {
    ImprintConfiguration.Environment.STAGING -> "https://apply.stg.imprintapi.co"
    ImprintConfiguration.Environment.SANDBOX -> "https://apply-sandbox.stg.imprintapi.co"
    ImprintConfiguration.Environment.PRODUCTION -> "https://apply.imprint.co"
  }
  val webUrl = "$host/start?session-token=${configuration.sessionToken}"

  private val _logoUrl = MutableStateFlow<String?>(null)
  val logoUrl: StateFlow<String?> = _logoUrl.asStateFlow()

  private var completionState: ImprintConfiguration.CompletionState =
    ImprintConfiguration.CompletionState.ABANDONED
  private var completionMetadata: Map<String, String>? = null

  fun updateLogoUrl(url: String) {
    _logoUrl.value = url
  }

  fun updateCompletionState(state: ImprintConfiguration.CompletionState, metadata: Map<String, String>?) {
    completionState = state
    completionMetadata = metadata
  }

  fun onDismiss() {
    configuration.onCompletion?.invoke(completionState, completionMetadata)
  }
}