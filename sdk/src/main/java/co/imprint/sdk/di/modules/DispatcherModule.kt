package co.imprint.sdk.di.modules

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatcherModule = module {
  single<CoroutineDispatcher>(named("DefaultDispatcher")) { Dispatchers.Default }
  single<CoroutineDispatcher>(named("IoDispatcher")) { Dispatchers.IO }
  single<CoroutineDispatcher>(named("MainDispatcher")) { Dispatchers.Main }
}