package co.imprint.sdk.domain.model

/**
 * Terminal states for the application process.
 *
 * Delivered via the `onCompletion` callback in [co.imprint.sdk.Imprint.startApplication].
 * The second parameter of the callback is a metadata map whose contents vary by state:
 *
 * - [OFFER_ACCEPTED]: metadata contains application result fields defined in the Imprint API docs.
 * - [REJECTED]: metadata contains rejection detail fields defined in the Imprint API docs.
 * - [ERROR]: metadata contains an `"error_code"` key mapped to an [ImprintErrorCode] value.
 * - [IN_PROGRESS]: metadata is `null` — the user dismissed the flow before reaching a terminal state.
 */
enum class ImprintCompletionState {
  /** The user was approved and accepted the offer. */
  OFFER_ACCEPTED,
  /** The application was rejected. */
  REJECTED,
  /** The user dismissed the flow before completing the application. */
  IN_PROGRESS,
  /** An error occurred during the application process. Check `"error_code"` in metadata for details. */
  ERROR,
}