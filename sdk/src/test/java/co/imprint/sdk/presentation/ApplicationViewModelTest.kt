package co.imprint.sdk.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import co.imprint.sdk.domain.ImprintCallbackHolder
import co.imprint.sdk.domain.model.ImprintCompletionState
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.domain.model.ImprintErrorCode
import co.imprint.sdk.domain.model.ImprintProcessState
import co.imprint.sdk.domain.repository.ImageRepository
import co.imprint.sdk.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationViewModelTest {

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private lateinit var viewModel: ApplicationViewModel
  private lateinit var savedStateHandle: SavedStateHandle
  private val validConfiguration = mockk<ImprintConfiguration>(relaxed = true)
  private val imageRepository = mockk<ImageRepository>(relaxed = true)

  @Before
  fun setup() {
    savedStateHandle = SavedStateHandle().apply {
      // Add the configuration to the state
      set(ApplicationActivity.APPLICATION_CONFIGURATION, validConfiguration)
    }
    viewModel = ApplicationViewModel(imageRepository, savedStateHandle)
  }

  @After
  fun tearDown() {
    ImprintCallbackHolder.onApplicationCompletion = null
    ImprintCallbackHolder.onApplicationEvent = null
  }

  @Test
  fun `view model initialization success when configuration is provided by state`() {
    assertNotNull(viewModel.webUrl)
  }

  @Test(expected = IllegalStateException::class)
  fun `view model initialization fails when configuration is not provided`() {
    // Simulate missing configuration by not setting it in SavedStateHandle
    savedStateHandle.remove<ImprintConfiguration>(ApplicationActivity.APPLICATION_CONFIGURATION)
    // Recreate the ViewModel with the missing configuration
    viewModel = ApplicationViewModel(imageRepository, savedStateHandle)
  }

  @Test
  fun `onDismiss called with null callback`() {
    ImprintCallbackHolder.onApplicationCompletion = null
    viewModel.onDismiss()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateLogoUrl should load image successfully`() = runTest {
    val bitmapImage = mockk<Bitmap>(relaxed = true)
    coEvery { imageRepository.getImageBitmap(any()) } returns bitmapImage
    viewModel.updateLogoUrl("https://example.com/logo.png")
    advanceUntilIdle()
    assertEquals(bitmapImage, viewModel.logoBitmap.value)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateLogoUrl call fails and live data remains null`() = runTest {
    coEvery { imageRepository.getImageBitmap(any()) } throws Exception()
    viewModel.updateLogoUrl("")
    advanceUntilIdle()
    assertEquals(null, viewModel.logoBitmap.value)
  }

  @Test
  fun `processResultData removes event name and timestamp fields`() {
    // Arrange
    val eventData = JSONObject().apply {
      put(Constants.EVENT_NAME, "SUCCESS")
      put(Constants.SOURCE, "MOBILE_SDK")
      put("other_field", "value")
    }
    val state = ImprintProcessState.OFFER_ACCEPTED

    // Act
    val result = viewModel.processResultData(eventData, state)

    // Assert
    assertFalse(result.containsKey(Constants.EVENT_NAME))
    assertFalse(result.containsKey(Constants.SOURCE))
    assertEquals("value", result["other_field"])
  }

  @Test
  fun `processResultData adds error code for ERROR state`() {
    // Arrange
    val eventData = JSONObject().apply {
      put(Constants.EVENT_NAME, "ERROR")
      put(Constants.ERROR_CODE, "INVALID_CLIENT_SECRET")
    }
    val state = ImprintProcessState.ERROR

    // Act
    val result = viewModel.processResultData(eventData, state)

    // Assert
    assertEquals(ImprintErrorCode.INVALID_CLIENT_SECRET, result["error_code"])
  }

  @Test
  fun `processResultData preserves other fields`() {
    // Arrange
    val eventData = JSONObject().apply {
      put(Constants.EVENT_NAME, "OFFER_ACCEPTED")
      put("field1", "value1")
      put("field2", 42)
      put("field3", true)
    }
    val state = ImprintProcessState.OFFER_ACCEPTED

    // Act
    val result = viewModel.processResultData(eventData, state)

    // Assert
    assertEquals("value1", result["field1"])
    assertEquals(42, result["field2"])
    assertEquals(true, result["field3"])
  }

  @Test
  fun `intermediate partner event is observable without overwriting accepted outcome`() {
    val receivedEvents = mutableListOf<Pair<String, Map<String, Any?>?>>()
    var completionState: ImprintCompletionState? = null
    var completionData: Map<String, Any?>? = null
    ImprintCallbackHolder.onApplicationEvent = { eventName, data ->
      receivedEvents.add(eventName to data)
    }
    ImprintCallbackHolder.onApplicationCompletion = { state, data ->
      completionState = state
      completionData = data
    }

    viewModel.processEventData(tieredEvent("OFFER_ACCEPTED", "outcome"))
    viewModel.processEventData(
      tieredEvent("ACCOUNT_LINK_RESULT", "intermediate").apply {
        put("status", "success")
      },
    )
    viewModel.processEventData(
      tieredEvent("CLOSED", "terminal", Constants.INTERNAL_SOURCE),
    )

    assertEquals(ImprintCompletionState.OFFER_ACCEPTED, completionState)
    assertEquals("outcome", completionData?.get(Constants.TIER))
    assertFalse(completionData?.containsKey("status") == true)
    assertEquals(listOf("OFFER_ACCEPTED", "ACCOUNT_LINK_RESULT"), receivedEvents.map { it.first })
    assertEquals("success", receivedEvents.last().second?.get("status"))
  }

  @Test
  fun `internal events stay private while updating shell lifecycle`() {
    val receivedEvents = mutableListOf<String>()
    var completionState: ImprintCompletionState? = null
    ImprintCallbackHolder.onApplicationEvent = { eventName, _ -> receivedEvents.add(eventName) }
    ImprintCallbackHolder.onApplicationCompletion = { state, _ -> completionState = state }

    viewModel.processEventData(
      tieredEvent("OFFER_ACCEPTED", "outcome", Constants.INTERNAL_SOURCE),
    )
    viewModel.processEventData(
      tieredEvent("CLOSED", "terminal", Constants.INTERNAL_SOURCE),
    )

    assertTrue(receivedEvents.isEmpty())
    assertEquals(ImprintCompletionState.OFFER_ACCEPTED, completionState)
  }

  @Test
  fun `unknown event and tier are observable but do not change completion`() {
    val receivedEvents = mutableListOf<String>()
    var completionState: ImprintCompletionState? = null
    ImprintCallbackHolder.onApplicationEvent = { eventName, _ -> receivedEvents.add(eventName) }
    ImprintCallbackHolder.onApplicationCompletion = { state, _ -> completionState = state }

    viewModel.processEventData(tieredEvent("OFFER_ACCEPTED", "outcome"))
    viewModel.processEventData(tieredEvent("FUTURE_EVENT", "outcome"))
    viewModel.processEventData(tieredEvent("CLOSED", "future_tier"))
    viewModel.onDismiss()

    assertEquals(ImprintCompletionState.OFFER_ACCEPTED, completionState)
    assertEquals(listOf("OFFER_ACCEPTED", "FUTURE_EVENT", "CLOSED"), receivedEvents)
  }

  @Test
  fun `legacy unknown event does not overwrite accepted outcome`() {
    var completionState: ImprintCompletionState? = null
    ImprintCallbackHolder.onApplicationCompletion = { state, _ -> completionState = state }

    viewModel.processEventData(JSONObject().put(Constants.EVENT_NAME, "OFFER_ACCEPTED"))
    viewModel.processEventData(JSONObject().put(Constants.EVENT_NAME, "ACCOUNT_LINK_RESULT"))
    viewModel.processEventData(JSONObject().put(Constants.EVENT_NAME, "CLOSED"))

    assertEquals(ImprintCompletionState.OFFER_ACCEPTED, completionState)
  }

  @Test
  fun `throwing onEvent callback does not drop lifecycle update`() {
    mockkStatic(Log::class)
    every { Log.e(any(), any(), any()) } returns 0
    var completionState: ImprintCompletionState? = null
    ImprintCallbackHolder.onApplicationEvent = { _, _ -> error("partner failure") }
    ImprintCallbackHolder.onApplicationCompletion = { state, _ -> completionState = state }

    try {
      viewModel.processEventData(tieredEvent("OFFER_ACCEPTED", "outcome"))
      viewModel.processEventData(tieredEvent("CLOSED", "terminal"))
    } finally {
      unmockkStatic(Log::class)
    }

    assertEquals(ImprintCompletionState.OFFER_ACCEPTED, completionState)
  }

  @Test
  fun `payload without source honors tier and reaches onEvent`() {
    val receivedEvents = mutableListOf<String>()
    var completionState: ImprintCompletionState? = null
    ImprintCallbackHolder.onApplicationEvent = { eventName, _ -> receivedEvents.add(eventName) }
    ImprintCallbackHolder.onApplicationCompletion = { state, _ -> completionState = state }

    viewModel.processEventData(tieredEvent("OFFER_ACCEPTED", "outcome").apply { remove(Constants.SOURCE) })
    viewModel.processEventData(tieredEvent("IN_PROGRESS", "intermediate").apply { remove(Constants.SOURCE) })
    viewModel.processEventData(tieredEvent("CLOSED", "terminal").apply { remove(Constants.SOURCE) })

    assertEquals(ImprintCompletionState.OFFER_ACCEPTED, completionState)
    assertEquals(listOf("OFFER_ACCEPTED", "IN_PROGRESS", "CLOSED"), receivedEvents)
  }

  private fun tieredEvent(
    eventName: String,
    tier: String,
    source: String = Constants.PARTNER_SOURCE,
  ) = JSONObject().apply {
    put(Constants.SOURCE, source)
    put(Constants.EVENT_NAME, eventName)
    put(Constants.TIER, tier)
  }
}
