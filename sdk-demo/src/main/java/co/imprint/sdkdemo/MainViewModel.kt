package co.imprint.sdkdemo

import android.content.Context
import androidx.lifecycle.ViewModel
import co.imprint.sdk.Imprint
import co.imprint.sdk.domain.model.ImprintCompletionState
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.domain.model.ImprintEnvironment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
  private val _clientSecret = MutableStateFlow("")
  val clientSecret: StateFlow<String> = _clientSecret

  private val _partnerReference = MutableStateFlow("")
  val partnerReference: StateFlow<String> = _partnerReference

  private val _selectedEnvironment = MutableStateFlow(Environment.STAGING)
  val selectedEnvironment: StateFlow<Environment> = _selectedEnvironment

  private val _completionState = MutableStateFlow("")
  val completionState: StateFlow<String> = _completionState

  fun updateClientSecret(value: String) {
    _clientSecret.value = value
  }

  fun updatePartnerReference(value: String) {
    _partnerReference.value = value
  }

  fun selectEnvironment(environment: Environment) {
    _selectedEnvironment.value = environment
  }

  fun startApplication(context: Context) {
    val environment = when (_selectedEnvironment.value) {
      Environment.STAGING -> ImprintEnvironment.STAGING
      Environment.SANDBOX -> ImprintEnvironment.SANDBOX
      Environment.PRODUCTION -> ImprintEnvironment.PRODUCTION
    }

    // Configure the Imprint SDK with the required parameters
    val configuration = ImprintConfiguration(
      clientSecret = _clientSecret.value,
      partnerReference = _partnerReference.value,
      environment = environment,
    )

    // Callback function triggered when the application process is completed
    val onCompletion =
      { state: ImprintCompletionState, metadata: Map<String, Any?>? ->
        val metadataInfo = metadata?.toString() ?: "No metadata"
        val resultText = when (state) {
          ImprintCompletionState.OFFER_ACCEPTED -> {
            "Offer accepted\n$metadataInfo"
          }

          ImprintCompletionState.REJECTED -> {
            "Application rejected\n$metadataInfo"
          }

          ImprintCompletionState.IN_PROGRESS -> {
            "Application abandoned"
          }

          ImprintCompletionState.ERROR -> {
            "Error occurred"
          }
        }
        _completionState.value = resultText
      }

    // Start the application process with the provided context, configuration, and callback
    Imprint.startApplication(
      context = context,
      configuration = configuration,
      onCompletion = onCompletion,
    )
  }
}

enum class Environment(val displayName: String) {
  STAGING("Staging"),
  SANDBOX("Sandbox"),
  PRODUCTION("Production");

  companion object {
    fun fromIndex(index: Int): Environment = entries[index]
    fun toIndex(environment: Environment): Int = entries.indexOf(environment)
  }
}