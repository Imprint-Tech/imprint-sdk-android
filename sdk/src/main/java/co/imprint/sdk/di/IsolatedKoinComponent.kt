package co.imprint.sdk.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

/**
 * Interface for accessing the SDK's isolated Koin context.
 *
 * Any class within the SDK that requires dependency injection **must** implement this interface
 * to ensure that dependencies are resolved from the SDK's isolated Koin instance rather than
 * the app's global Koin instance.
 *
 * This prevents conflicts when the host app also uses Koin for dependency management.
 */
internal interface IsolatedKoinComponent : KoinComponent {
  override fun getKoin(): Koin = SdkIsolatedKoinContext.koin
}