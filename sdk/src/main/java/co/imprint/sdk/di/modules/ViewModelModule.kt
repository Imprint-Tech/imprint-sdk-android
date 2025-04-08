package co.imprint.sdk.di.modules

import co.imprint.sdk.presentation.ApplicationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
  viewModel { parameters ->
    ApplicationViewModel(
      imageRepository = get(),
      state = parameters.get(),
    )
  }
}