package co.imprint.imprintsdk.application

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Configuration settings for the Imprint application process.
 */
@Parcelize
data class ImprintConfiguration(
  val sessionToken: String,
  val environment: Environment = Environment.PRODUCTION,
  var externalReferenceId: String? = null,
  var applicationId: String? = null,
  var additionalData: Map<String, String>? = null,
  var onCompletion: ((CompletionState, CompletionMetadata?) -> Unit)? = null
) : Parcelable {

  /**
   * Available environments for the application process.
   */
  enum class Environment {
    STAGING, SANDBOX, PRODUCTION
  }

  /**
   * Terminal states for the application process.
   */
  enum class CompletionState {
    OFFER_ACCEPTED, REJECTED, ABANDONED
  }
}

/**
 * Metadata dictionary passed to the completion handler, containing flexible key-value pairs.
 */
typealias CompletionMetadata = Map<String, Any>