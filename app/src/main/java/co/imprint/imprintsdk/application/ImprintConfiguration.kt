package co.imprint.imprintsdk.application

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Configuration settings for the Imprint application process.
 */
@Parcelize
data class ImprintConfiguration(
  val client_secret: String,
  val environment: Environment = Environment.PRODUCTION,
  var application_id: String? = null,
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
    OFFER_ACCEPTED, REJECTED, ABANDONED, ERROR;

    companion object {
      fun fromString(value: String?): CompletionState {
        return try {
          value?.let { enumValueOf<CompletionState>(it) } ?: ABANDONED
        } catch (e: IllegalArgumentException) {
          ABANDONED
        }
      }
    }
  }
}