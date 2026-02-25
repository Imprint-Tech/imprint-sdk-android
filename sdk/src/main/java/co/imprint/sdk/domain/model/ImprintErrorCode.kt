package co.imprint.sdk.domain.model

/**
 * Error codes delivered in the `"error_code"` metadata key when the completion state is
 * [ImprintCompletionState.ERROR].
 */
enum class ImprintErrorCode(val code: String) {
  /** The provided client secret is invalid or expired. */
  INVALID_CLIENT_SECRET("INVALID_CLIENT_SECRET"),
  /** An unexpected error occurred. */
  UNKNOWN_ERROR("UNKNOWN_ERROR");

  companion object {
    /**
     * Converts a string value to its corresponding ErrorCode enum value.
     * Returns UNKNOWN_ERROR if no matching enum is found.
     *
     * @param value The string representation of the error code
     * @return The matching ErrorCode enum or UNKNOWN_ERROR if not found
     */
    fun fromString(value: String?): ImprintErrorCode {
      return entries.find { it.code == value } ?: UNKNOWN_ERROR
    }
  }
}