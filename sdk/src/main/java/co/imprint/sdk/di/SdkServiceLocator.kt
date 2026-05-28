package co.imprint.sdk.di

import androidx.lifecycle.SavedStateHandle
import co.imprint.sdk.data.repository.ImageRepositoryImpl
import co.imprint.sdk.domain.repository.ImageRepository
import co.imprint.sdk.presentation.ApplicationViewModel
import kotlinx.coroutines.Dispatchers

internal object SdkServiceLocator {

  val imageRepository: ImageRepository by lazy {
    ImageRepositoryImpl(Dispatchers.IO)
  }

  fun createViewModel(state: SavedStateHandle): ApplicationViewModel {
    return ApplicationViewModel(imageRepository, state)
  }
}
