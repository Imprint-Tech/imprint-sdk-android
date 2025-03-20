package co.imprint.sdk.di

import androidx.lifecycle.SavedStateHandle
import co.imprint.sdk.di.modules.dispatcherModule
import co.imprint.sdk.di.modules.repositoryModule
import co.imprint.sdk.di.modules.viewModelModule
import co.imprint.sdk.domain.repository.ImageRepository
import co.imprint.sdk.presentation.ApplicationViewModel
import org.junit.Test
import org.koin.core.Koin
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

class DiTest : KoinTest {

  override fun getKoin(): Koin = SdkIsolatedKoinContext.koin

  @OptIn(KoinExperimentalAPI::class)
  @Test
  fun `check all modules are correctly initialized`() {

    dispatcherModule.verify()
    repositoryModule.verify()
    viewModelModule.verify(
      injections = injectedParameters(
        definition<ApplicationViewModel>(ImageRepository::class, SavedStateHandle::class)
      )
    )
  }
}