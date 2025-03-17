package co.imprint.sdk.domain.model

/**
 * Available environments for the application process.
 */
enum class ImprintEnvironment(internal val hostUrl: String) {
  STAGING(hostUrl = "https://apply.stg.imprintapi.co"),
  SANDBOX(hostUrl = "https://apply.sbx.imprint.co"),
  PRODUCTION(hostUrl = "https://apply.imprint.co")
}