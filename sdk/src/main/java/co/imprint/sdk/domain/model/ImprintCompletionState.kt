package co.imprint.sdk.domain.model

/**
 * Terminal states for the application process.
 *
 * Possible values are [OFFER_ACCEPTED], [REJECTED], [IN_PROGRESS], [ERROR].
 */
enum class ImprintCompletionState {
  OFFER_ACCEPTED,
  REJECTED,
  IN_PROGRESS,
  ERROR,
}