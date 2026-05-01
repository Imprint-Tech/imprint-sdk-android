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
    assertTrue(config.webUrl.contains("offerConfigUUID=sbx-offer-uuid"))
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
}
