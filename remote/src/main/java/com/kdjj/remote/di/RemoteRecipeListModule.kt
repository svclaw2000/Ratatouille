package com.kdjj.remote.di

import com.kdjj.data.datasource.RecipeListRemoteDataSource
import com.kdjj.remote.datasource.RecipeListRemoteDataSourceImpl
import com.kdjj.remote.service.RecipeListServiceImpl
import com.kdjj.remote.service.RemoteRecipeListService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteRecipeListModule {
    
    @Binds
    @Singleton
    internal abstract fun bindRecipeListRemoteDataSource(
        recipeListRemoteDataSourceImpl: RecipeListRemoteDataSourceImpl
    ): RecipeListRemoteDataSource
    
    @Binds
    @Singleton
    internal abstract fun bindRecipeListDao(
        recipeListDaoImpl: RecipeListServiceImpl
    ): RemoteRecipeListService
}
