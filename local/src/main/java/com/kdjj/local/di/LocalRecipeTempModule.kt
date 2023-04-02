package com.kdjj.local.di

import com.kdjj.data.datasource.RecipeTempLocalDataSource
import com.kdjj.local.dataSource.RecipeTempLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocalRecipeTempModule {
    
    @Binds
    @Singleton
    internal abstract fun bindRecipeTempLocalDataSource(
        recipeTempLocalDataSourceImpl: RecipeTempLocalDataSourceImpl
    ): RecipeTempLocalDataSource
}
