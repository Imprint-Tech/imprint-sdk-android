package co.imprint.sdk.domain.model

import org.junit.Test
import org.junit.Assert.assertEquals

class ImprintStateTests {

  @Test
  fun `test ImprintProcessState to ImprintCompletionState conversion`() {
    // Test direct mappings
    assertEquals(ImprintCompletionState.OFFER_ACCEPTED, ImprintProcessState.OFFER_ACCEPTED.toCompletionState())
    assertEquals(ImprintCompletionState.REJECTED, ImprintProcessState.REJECTED.toCompletionState())
    assertEquals(ImprintCompletionState.ERROR, ImprintProcessState.ERROR.toCompletionState())

    // Test states that map to ABANDONED
    assertEquals(ImprintCompletionState.IN_PROGRESS, ImprintProcessState.INITIALIZED.toCompletionState())
    assertEquals(ImprintCompletionState.IN_PROGRESS, ImprintProcessState.APPLICATION_STARTED.toCompletionState())
    assertEquals(ImprintCompletionState.IN_PROGRESS, ImprintProcessState.OFFER_PRESENTED.toCompletionState())
    assertEquals(ImprintCompletionState.IN_PROGRESS, ImprintProcessState.OFFER_DECLINED.toCompletionState())
    assertEquals(ImprintCompletionState.IN_PROGRESS, ImprintProcessState.APPLICATION_REVIEW.toCompletionState())
    assertEquals(ImprintCompletionState.IN_PROGRESS, ImprintProcessState.CREDIT_FROZEN.toCompletionState())
    assertEquals(ImprintCompletionState.IN_PROGRESS, ImprintProcessState.ABANDONED.toCompletionState())
  }

  @Test
  fun `test ImprintProcessState fromString valid values`() {
    // Test valid conversions
    assertEquals(ImprintProcessState.INITIALIZED, ImprintProcessState.fromString("INITIALIZED"))
    assertEquals(ImprintProcessState.APPLICATION_STARTED, ImprintProcessState.fromString("APPLICATION_STARTED"))
    assertEquals(ImprintProcessState.OFFER_PRESENTED, ImprintProcessState.fromString("OFFER_PRESENTED"))
    assertEquals(ImprintProcessState.OFFER_ACCEPTED, ImprintProcessState.fromString("OFFER_ACCEPTED"))
    assertEquals(ImprintProcessState.OFFER_DECLINED, ImprintProcessState.fromString("OFFER_DECLINED"))
    assertEquals(ImprintProcessState.REJECTED, ImprintProcessState.fromString("REJECTED"))
    assertEquals(ImprintProcessState.APPLICATION_REVIEW, ImprintProcessState.fromString("APPLICATION_REVIEW"))
    assertEquals(ImprintProcessState.CREDIT_FROZEN, ImprintProcessState.fromString("CREDIT_FROZEN"))
    assertEquals(ImprintProcessState.ABANDONED, ImprintProcessState.fromString("ABANDONED"))
    assertEquals(ImprintProcessState.ERROR, ImprintProcessState.fromString("ERROR"))
  }

  @Test
  fun `test ImprintProcessState fromString edge cases`() {
    // Test null handling
    assertEquals(ImprintProcessState.ABANDONED, ImprintProcessState.fromString(null))

    // Test invalid values
    assertEquals(ImprintProcessState.ABANDONED, ImprintProcessState.fromString("INVALID_STATE"))
    assertEquals(ImprintProcessState.ABANDONED, ImprintProcessState.fromString(""))
    assertEquals(ImprintProcessState.ABANDONED, ImprintProcessState.fromString("offer_accepted")) // Case sensitivity
  }
}