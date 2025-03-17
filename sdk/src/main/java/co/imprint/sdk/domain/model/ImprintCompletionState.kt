package co.imprint.sdk.domain.model

/**
 * Terminal states for the application process.
 *
 * Possible values are [OFFER_ACCEPTED], [REJECTED], [ABANDONED], [ERROR].
 */
enum class ImprintCompletionState {
  OFFER_ACCEPTED, REJECTED, ABANDONED, ERROR;

  companion object {
    fun fromString(value: String?): ImprintCompletionState {
      return try {
        value?.let { enumValueOf<ImprintCompletionState>(it) } ?: ABANDONED
      } catch (e: IllegalArgumentException) {
        ABANDONED
      }
    }
  }
}