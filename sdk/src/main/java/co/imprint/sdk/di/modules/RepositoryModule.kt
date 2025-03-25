package co.imprint.sdk.di.modules

import co.imprint.sdk.data.repository.ImageRepositoryImpl
import co.imprint.sdk.domain.repository.ImageRepository
import org.koin.dsl.module

val repositoryModule = module {
  factory<ImageRepository> { ImageRepositoryImpl(get()) }
}