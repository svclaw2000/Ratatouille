package com.kdjj.presentation.di

import com.kdjj.domain.common.AuthorIdProvider
import com.kdjj.presentation.common.AuthorIdProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PresentationModule {

    @Binds
    @Singleton
    internal abstract fun bindAuthorIdProvider(
        authorIdProviderImpl: AuthorIdProviderImpl
    ) : AuthorIdProvider
}