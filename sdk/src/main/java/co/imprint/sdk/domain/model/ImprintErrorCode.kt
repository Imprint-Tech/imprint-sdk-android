package co.imprint.sdk.domain.model

enum class ImprintErrorCode(val code: String) {
  // Authentication errors
  INVALID_CLIENT_SECRET("INVALID_CLIENT_SECRET"),

  // Network errors
  NETWORK_CONNECTION_FAILED("NETWORK_CONNECTION_FAILED"),
  SERVER_ERROR("SERVER_ERROR"),
  TIMEOUT_ERROR("TIMEOUT_ERROR"),

  // Fallback
  UNKNOWN("UNKNOWN_ERROR");

  companion object {
    /**
     * Converts a string value to its corresponding ErrorCode enum value.
     * Returns UNKNOWN if no matching enum is found.
     *
     * @param value The string representation of the error code
     * @return The matching ErrorCode enum or UNKNOWN if not found
     */
    fun fromString(value: String?): ImprintErrorCode {
      return entries.find { it.code == value } ?: UNKNOWN
    }
  }
}