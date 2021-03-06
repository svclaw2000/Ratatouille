package com.kdjj.local.dataSource

import com.kdjj.data.datasource.RecipeListLocalDataSource
import com.kdjj.domain.model.Recipe
import com.kdjj.local.dao.RecipeListDao
import com.kdjj.local.mapper.toDomain
import javax.inject.Inject

internal class RecipeListLocalDataSourceImpl @Inject constructor(
    private val recipeListDao: RecipeListDao,
) : RecipeListLocalDataSource {

    override suspend fun fetchLatestRecipeListAfter(index: Int): Result<List<Recipe>> =
        runCatching {
            recipeListDao.fetchLatestRecipeList(PAGE_SIZE, index)
                .map { it.toDomain() }
        }

    override suspend fun fetchFavoriteRecipeListAfter(index: Int): Result<List<Recipe>> =
        runCatching {
            recipeListDao.fetchFavoriteRecipeList(PAGE_SIZE, index)
                .map { it.toDomain() }
        }

    override suspend fun fetchSearchRecipeListAfter(
        keyword: String,
        index: Int
    ): Result<List<Recipe>> =
        runCatching {
            recipeListDao.fetchSearchRecipeList(PAGE_SIZE, "%$keyword%", index)
                .map { it.toDomain() }
        }

    override suspend fun fetchTitleListAfter(
        index: Int
    ): Result<List<Recipe>> =
        runCatching {
            recipeListDao.fetchTitleRecipeList(PAGE_SIZE, index)
                .map { it.toDomain() }
        }

    companion object {
        const val PAGE_SIZE = 10
    }
}
