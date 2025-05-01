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
 */
@Parcelize
data class ImprintConfiguration(
  val clientSecret: String,
  val environment: ImprintEnvironment = ImprintEnvironment.PRODUCTION,
) : Parcelable {

  @IgnoredOnParcel
  internal val webUrl: String =
    "${environment.hostUrl}/start?client_secret=${clientSecret}"
}