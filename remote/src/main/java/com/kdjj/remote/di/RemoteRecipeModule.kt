package com.kdjj.remote.di

import com.kdjj.data.datasource.RecipeRemoteDataSource
import com.kdjj.remote.datasource.RecipeRemoteDataSourceImpl
import com.kdjj.remote.service.RecipeServiceImpl
import com.kdjj.remote.service.RemoteRecipeService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteRecipeModule {
    
    @Binds
    @Singleton
    internal abstract fun bindRecipeRemoteDataSource(
        recipeRemoteDataSourceImpl: RecipeRemoteDataSourceImpl
    ): RecipeRemoteDataSource
    
    @Binds
    @Singleton
    internal abstract fun provideRecipeDao(
        recipeDaoImpl: RecipeServiceImpl
    ): RemoteRecipeService
}
