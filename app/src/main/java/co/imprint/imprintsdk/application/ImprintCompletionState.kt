package co.imprint.imprintsdk.application

/**
 * Terminal states for the application process.
 *
 * Possible values are [OFFER_ACCEPTED], [REJECTED], [ABANDONED], [ERROR].
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