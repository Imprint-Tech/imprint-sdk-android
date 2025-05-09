package co.imprint.sdk.domain.model


import org.junit.Assert.assertEquals
import org.junit.Test

class ImprintErrorCodeTest {
  @Test
  fun `fromString returns correct enum for valid error code strings`() {
    // Test each defined error code
    assertEquals(ImprintErrorCode.INVALID_CLIENT_SECRET, ImprintErrorCode.fromString("INVALID_CLIENT_SECRET"))
    assertEquals(ImprintErrorCode.UNKNOWN_ERROR, ImprintErrorCode.fromString("UNKNOWN_ERROR"))
  }

  @Test
  fun `fromString returns UNKNOWN for null input`() {
    assertEquals(ImprintErrorCode.UNKNOWN_ERROR, ImprintErrorCode.fromString(null))
  }

  @Test
  fun `fromString returns UNKNOWN for empty string`() {
    assertEquals(ImprintErrorCode.UNKNOWN_ERROR, ImprintErrorCode.fromString(""))
  }

  @Test
  fun `fromString returns UNKNOWN for unrecognized error codes`() {
    assertEquals(ImprintErrorCode.UNKNOWN_ERROR, ImprintErrorCode.fromString("NOT_A_REAL_ERROR"))
    assertEquals(ImprintErrorCode.UNKNOWN_ERROR, ImprintErrorCode.fromString("SERVER_FAILURE")) // Similar but not matching
    assertEquals(ImprintErrorCode.UNKNOWN_ERROR, ImprintErrorCode.fromString("unknown_error")) // Case sensitive
  }

  @Test
  fun `fromString handles case sensitivity correctly`() {
    // Should not match because enum codes are all uppercase
    assertEquals(ImprintErrorCode.UNKNOWN_ERROR, ImprintErrorCode.fromString("server_error"))
    assertEquals(ImprintErrorCode.UNKNOWN_ERROR, ImprintErrorCode.fromString("Server_Error"))
  }
}