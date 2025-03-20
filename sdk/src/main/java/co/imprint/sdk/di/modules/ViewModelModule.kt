package co.imprint.sdk.di.modules

import androidx.lifecycle.SavedStateHandle
import co.imprint.sdk.presentation.ApplicationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
  viewModel { (state: SavedStateHandle) ->
    ApplicationViewModel(ioDispatcher = get(named("IoDispatcher")), state = state)
  }
}