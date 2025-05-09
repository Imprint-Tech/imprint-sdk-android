package co.imprint.sdk.domain.model

enum class ImprintErrorCode(val code: String) {
  INVALID_CLIENT_SECRET("INVALID_CLIENT_SECRET"),
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