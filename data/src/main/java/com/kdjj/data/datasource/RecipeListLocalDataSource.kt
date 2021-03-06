package com.kdjj.data.datasource

import com.kdjj.domain.model.Recipe

interface RecipeListLocalDataSource {
    
    suspend fun fetchLatestRecipeListAfter(
        index: Int
    ): Result<List<Recipe>>
    
    suspend fun fetchFavoriteRecipeListAfter(
        index: Int
    ): Result<List<Recipe>>
    
    suspend fun fetchSearchRecipeListAfter(
        keyword: String,
        index: Int
    ): Result<List<Recipe>>

    suspend fun fetchTitleListAfter(
        index: Int
    ): Result<List<Recipe>>
}
