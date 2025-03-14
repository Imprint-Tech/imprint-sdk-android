package co.imprint.sdk.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Configuration settings for the Imprint application process.
 *
 * @param clientSecret The client secret used to initiate the application session. Generated through Create Customer Session.
 * Please refer to the API documentation (https://docs.imprint.co/api-reference/customer-sessions/create-a-new-customer-session) for details on obtaining a clientSecret.
 * @param partnerReference The unique identifier for the program provided by the Imprint team.
 * @param environment The environment for the application process. Default value is [Environment.PRODUCTION].
 */
@Parcelize
data class ImprintConfiguration(
  val clientSecret: String,
  val partnerReference: String,
  val environment: Environment = Environment.PRODUCTION,
) : Parcelable {

  /**
   * Available environments for the application process.
   */
  enum class Environment {
    STAGING, SANDBOX, PRODUCTION
  }
}