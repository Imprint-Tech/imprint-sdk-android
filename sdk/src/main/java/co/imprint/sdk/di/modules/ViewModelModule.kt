package co.imprint.sdk.di.modules

import androidx.lifecycle.SavedStateHandle
import co.imprint.sdk.domain.repository.ImageRepository
import co.imprint.sdk.presentation.ApplicationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
  viewModel { (state: SavedStateHandle, imageRepository: ImageRepository) ->
    ApplicationViewModel(
      imageRepository = imageRepository,
      state = state
    )
  }
}