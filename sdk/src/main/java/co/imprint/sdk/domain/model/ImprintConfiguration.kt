package co.imprint.sdk.domain.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Configuration settings for the Imprint application process.
 *
 * @param clientSecret The client secret used to initiate the application session. Generated through Create Customer Session.
 * Please refer to the API documentation (https://docs.imprint.co/api-reference/customer-sessions/create-a-new-customer-session) for details on obtaining a clientSecret.
 * @param environment The environment for the application process. Default value is [ImprintEnvironment.PRODUCTION].
 * @param offerConfigUUID A single offer configuration UUID to apply to the application.
 * @param offerConfigUUIDs Offer configuration UUIDs to apply to the application. When it contains at least one
 * non-blank value, it takes precedence over [offerConfigUUID].
 */
@Parcelize
data class ImprintConfiguration(
  val clientSecret: String,
  val environment: ImprintEnvironment = ImprintEnvironment.PRODUCTION,
  val offerConfigUUID: String? = null,
  val offerConfigUUIDs: List<String>? = null,
  internal val customHostUrl: String? = null,
) : Parcelable {

  @IgnoredOnParcel
  internal val webUrl: String = buildString {
    append("${customHostUrl ?: environment.hostUrl}/start?client_secret=${clientSecret}")
    resolvedOfferConfigUUIDs()?.let { append("&offerConfigUUIDs=$it") }
  }

  private fun resolvedOfferConfigUUIDs(): String? {
    val list = offerConfigUUIDs?.filter { it.isNotBlank() }
    if (!list.isNullOrEmpty()) return list.joinToString(",")
    return offerConfigUUID?.takeIf { it.isNotBlank() }
  }
}