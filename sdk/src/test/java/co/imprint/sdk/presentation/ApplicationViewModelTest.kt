package co.imprint.sdk.presentation

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import co.imprint.sdk.domain.ImprintCallbackHolder
import co.imprint.sdk.domain.model.ImprintConfiguration
import co.imprint.sdk.domain.repository.ImageRepository
import co.imprint.sdk.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
    viewModel.updateLogoUrl("")
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


}
