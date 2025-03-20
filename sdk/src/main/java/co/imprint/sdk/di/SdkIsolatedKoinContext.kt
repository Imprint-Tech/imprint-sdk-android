package co.imprint.sdk.di

import co.imprint.sdk.di.modules.dispatcherModule
import co.imprint.sdk.di.modules.repositoryModule
import co.imprint.sdk.di.modules.viewModelModule
import org.koin.dsl.koinApplication

/**
 * This object holds the instance of our isolated Koin context.
 * Since we are developing an SDK, this is necessary because an Android app can only have
 * a single global instance of Koin. By isolating our context, we avoid conflicts with
 * apps that also use Koin alongside our SDK.
 *
 * For more details, refer to:
 * https://insert-koin.io/docs/reference/koin-core/context-isolation/
 */
internal object SdkIsolatedKoinContext {

  private val koinApp = koinApplication {
    // declare used modules
    modules(dispatcherModule, viewModelModule, repositoryModule)
  }

  val koin = koinApp.koin
}