package co.imprint.sdk.domain.model

import junit.framework.TestCase.assertEquals
import org.junit.Test

class ImprintCompletionStateTest {

  @Test
  fun testValidStringToEnum() {
    // Test for valid enum strings
    assertEquals(ImprintCompletionState.OFFER_ACCEPTED, ImprintCompletionState.fromString("OFFER_ACCEPTED"))
    assertEquals(ImprintCompletionState.REJECTED, ImprintCompletionState.fromString("REJECTED"))
    assertEquals(ImprintCompletionState.ABANDONED, ImprintCompletionState.fromString("ABANDONED"))
    assertEquals(ImprintCompletionState.ERROR, ImprintCompletionState.fromString("ERROR"))
  }

  @Test
  fun testInvalidStringToEnum() {
    // Test for invalid enum strings
    assertEquals(ImprintCompletionState.ABANDONED, ImprintCompletionState.fromString("INVALID"))
  }

  @Test
  fun testNullStringToEnum() {
    // Test for null value
    assertEquals(ImprintCompletionState.ABANDONED, ImprintCompletionState.fromString(null))
  }

  @Test
  fun testEmptyStringToEnum() {
    // Test for empty string
    assertEquals(ImprintCompletionState.ABANDONED, ImprintCompletionState.fromString(""))
  }
}