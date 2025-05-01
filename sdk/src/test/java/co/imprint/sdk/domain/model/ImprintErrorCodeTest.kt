package co.imprint.sdk.domain.model


import org.junit.Assert.assertEquals
import org.junit.Test

class ImprintErrorCodeTest {
  @Test
  fun `fromString returns correct enum for valid error code strings`() {
    // Test each defined error code
    assertEquals(ImprintErrorCode.INVALID_CLIENT_SECRET, ImprintErrorCode.fromString("INVALID_CLIENT_SECRET"))
    assertEquals(ImprintErrorCode.INVALID_PARTNER_REFERENCE, ImprintErrorCode.fromString("INVALID_PARTNER_REFERENCE"))
    assertEquals(ImprintErrorCode.NETWORK_CONNECTION_FAILED, ImprintErrorCode.fromString("NETWORK_CONNECTION_FAILED"))
    assertEquals(ImprintErrorCode.SERVER_ERROR, ImprintErrorCode.fromString("SERVER_ERROR"))
    assertEquals(ImprintErrorCode.TIMEOUT_ERROR, ImprintErrorCode.fromString("TIMEOUT_ERROR"))
    assertEquals(ImprintErrorCode.UNKNOWN, ImprintErrorCode.fromString("UNKNOWN_ERROR"))
  }

  @Test
  fun `fromString returns UNKNOWN for null input`() {
    assertEquals(ImprintErrorCode.UNKNOWN, ImprintErrorCode.fromString(null))
  }

  @Test
  fun `fromString returns UNKNOWN for empty string`() {
    assertEquals(ImprintErrorCode.UNKNOWN, ImprintErrorCode.fromString(""))
  }

  @Test
  fun `fromString returns UNKNOWN for unrecognized error codes`() {
    assertEquals(ImprintErrorCode.UNKNOWN, ImprintErrorCode.fromString("NOT_A_REAL_ERROR"))
    assertEquals(ImprintErrorCode.UNKNOWN, ImprintErrorCode.fromString("SERVER_FAILURE")) // Similar but not matching
    assertEquals(ImprintErrorCode.UNKNOWN, ImprintErrorCode.fromString("unknown_error")) // Case sensitive
  }

  @Test
  fun `fromString handles case sensitivity correctly`() {
    // Should not match because enum codes are all uppercase
    assertEquals(ImprintErrorCode.UNKNOWN, ImprintErrorCode.fromString("server_error"))
    assertEquals(ImprintErrorCode.UNKNOWN, ImprintErrorCode.fromString("Server_Error"))
  }
}