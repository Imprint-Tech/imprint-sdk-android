package co.imprint.sdk.di

import co.imprint.sdk.presentation.ApplicationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
  viewModelOf(::ApplicationViewModel)
}