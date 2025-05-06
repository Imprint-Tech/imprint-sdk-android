package co.imprint.sdk.domain.model

import org.junit.Test
import org.junit.Assert.assertEquals

class ImprintStateTests {

  @Test
  fun `test ImprintProcessState to ImprintCompletionState conversion`() {
    // Test direct mappings
    assertEquals(ImprintCompletionState.OFFER_ACCEPTED, ImprintProcessState.OFFER_ACCEPTED.toCompletionState())
    assertEquals(ImprintCompletionState.REJECTED, ImprintProcessState.REJECTED.toCompletionState())
    assertEquals(ImprintCompletionState.IN_PROGRESS, ImprintProcessState.IN_PROGRESS.toCompletionState())
    assertEquals(ImprintCompletionState.ERROR, ImprintProcessState.ERROR.toCompletionState())
    assertEquals(ImprintCompletionState.IN_PROGRESS, ImprintProcessState.CLOSED.toCompletionState())
  }

  @Test
  fun `test ImprintProcessState fromString valid values`() {
    // Test valid conversions
    assertEquals(ImprintProcessState.OFFER_ACCEPTED, ImprintProcessState.fromString("OFFER_ACCEPTED"))
    assertEquals(ImprintProcessState.REJECTED, ImprintProcessState.fromString("REJECTED"))
    assertEquals(ImprintProcessState.CLOSED, ImprintProcessState.fromString("CLOSED"))
    assertEquals(ImprintProcessState.IN_PROGRESS, ImprintProcessState.fromString("IN_PROGRESS"))
    assertEquals(ImprintProcessState.ERROR, ImprintProcessState.fromString("ERROR"))
  }

  @Test
  fun `test ImprintProcessState fromString edge cases`() {
    // Test null handling
    assertEquals(null, ImprintProcessState.fromString(null))

    // Test invalid values
    assertEquals(null, ImprintProcessState.fromString("INVALID_STATE"))
    assertEquals(null, ImprintProcessState.fromString(""))
    assertEquals(null, ImprintProcessState.fromString("offer_accepted")) // Case sensitivity
  }
}