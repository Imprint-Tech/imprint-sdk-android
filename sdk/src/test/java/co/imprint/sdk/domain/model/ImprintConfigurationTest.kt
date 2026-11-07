package co.imprint.sdk.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class ImprintConfigurationTest {

  @Test
  fun `webUrl does not contain offerConfigUUID when null`() {
    val config = ImprintConfiguration(
      clientSecret = "test-secret",
      environment = ImprintEnvironment.PRODUCTION,
    )

    assertEquals(
      "https://apply.imprint.co/start?client_secret=test-secret",
      config.webUrl,
    )
    assertFalse(config.webUrl.contains("offerConfigUUIDs"))
  }

  @Test
  fun `webUrl contains offerConfigUUID when provided`() {
    val config = ImprintConfiguration(
      clientSecret = "test-secret",
      environment = ImprintEnvironment.PRODUCTION,
      offerConfigUUID = "offer-uuid-123",
    )

    assertEquals(
      "https://apply.imprint.co/start?client_secret=test-secret&offerConfigUUIDs=offer-uuid-123",
      config.webUrl,
    )
  }

  @Test
  fun `webUrl with sandbox environment and offerConfigUUID`() {
    val config = ImprintConfiguration(
      clientSecret = "sbx-secret",
      environment = ImprintEnvironment.SANDBOX,
      offerConfigUUID = "sbx-offer-uuid",
    )

    assertTrue(config.webUrl.startsWith("https://apply.sbx.imprint.co/start?"))
    assertTrue(config.webUrl.contains("client_secret=sbx-secret"))
    assertTrue(config.webUrl.contains("offerConfigUUIDs=sbx-offer-uuid"))
  }

  @Test
  fun `offerConfigUUID defaults to null`() {
    val config = ImprintConfiguration(clientSecret = "secret")
    assertNull(config.offerConfigUUID)
  }

  @Test
  fun `webUrl with custom host and offerConfigUUID`() {
    val config = ImprintConfiguration(
      clientSecret = "test-secret",
      offerConfigUUID = "custom-offer-uuid",
      customHostUrl = "https://custom.example.com",
    )

    assertEquals(
      "https://custom.example.com/start?client_secret=test-secret&offerConfigUUIDs=custom-offer-uuid",
      config.webUrl,
    )
  }

  @Test
  fun `webUrl joins offerConfigUUIDs with commas`() {
    val config = ImprintConfiguration(
      clientSecret = "test-secret",
      offerConfigUUIDs = listOf("uuid-1", "uuid-2", "uuid-3"),
    )

    assertEquals(
      "https://apply.imprint.co/start?client_secret=test-secret&offerConfigUUIDs=uuid-1,uuid-2,uuid-3",
      config.webUrl,
    )
  }

  @Test
  fun `offerConfigUUIDs takes precedence over offerConfigUUID`() {
    val config = ImprintConfiguration(
      clientSecret = "test-secret",
      offerConfigUUID = "single-uuid",
      offerConfigUUIDs = listOf("uuid-1", "uuid-2"),
    )

    assertEquals(
      "https://apply.imprint.co/start?client_secret=test-secret&offerConfigUUIDs=uuid-1,uuid-2",
      config.webUrl,
    )
  }

  @Test
  fun `empty offerConfigUUIDs falls back to offerConfigUUID`() {
    val config = ImprintConfiguration(
      clientSecret = "test-secret",
      offerConfigUUID = "single-uuid",
      offerConfigUUIDs = emptyList(),
    )

    assertEquals(
      "https://apply.imprint.co/start?client_secret=test-secret&offerConfigUUIDs=single-uuid",
      config.webUrl,
    )
  }

  @Test
  fun `blank offerConfigUUIDs entries are dropped`() {
    val config = ImprintConfiguration(
      clientSecret = "test-secret",
      offerConfigUUIDs = listOf("", "uuid-1", "  "),
    )

    assertEquals(
      "https://apply.imprint.co/start?client_secret=test-secret&offerConfigUUIDs=uuid-1",
      config.webUrl,
    )
  }

  @Test
  fun `webUrl omits offerConfigUUIDs when every value is blank`() {
    val config = ImprintConfiguration(
      clientSecret = "test-secret",
      offerConfigUUID = "",
      offerConfigUUIDs = listOf(""),
    )

    assertEquals("https://apply.imprint.co/start?client_secret=test-secret", config.webUrl)
  }

  @Test
  fun `offerConfigUUIDs defaults to null`() {
    val config = ImprintConfiguration(clientSecret = "secret")
    assertNull(config.offerConfigUUIDs)
  }
}
