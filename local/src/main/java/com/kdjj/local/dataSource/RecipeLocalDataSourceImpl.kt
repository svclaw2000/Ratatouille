package com.kdjj.local.dataSource

import androidx.room.withTransaction
import com.kdjj.data.datasource.RecipeLocalDataSource
import com.kdjj.domain.model.Recipe
import com.kdjj.domain.model.exception.NotExistRecipeException
import com.kdjj.local.dao.RecipeDao
import com.kdjj.local.dao.UselessImageDao
import com.kdjj.local.database.RecipeDatabase
import com.kdjj.local.dto.UselessImageDto
import com.kdjj.local.mapper.toDomain
import com.kdjj.local.mapper.toDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RecipeLocalDataSourceImpl @Inject constructor(
    private val recipeDatabase: RecipeDatabase,
    private val recipeDao: RecipeDao,
    private val uselessImageDao: UselessImageDao
) : RecipeLocalDataSource {

    override suspend fun saveRecipe(
        recipe: Recipe
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDatabase.withTransaction {
                    uselessImageDao.deleteUselessImage(
                        recipe.stepList
                            .map { it.imgPath }
                            .plus(recipe.imgPath),
                    )
                    recipeDao.deleteStepList(recipe.recipeId)
                    recipeDao.insertRecipeMeta(recipe.toDto())
                    recipe.stepList.forEachIndexed { idx, recipeStep ->
                        recipeDao.insertRecipeStep(recipeStep.toDto(recipe.recipeId, idx + 1))
                    }
                }
            }
        }

    override suspend fun updateRecipe(
        recipe: Recipe
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDao.updateRecipeMeta(recipe.toDto())
            }
        }

    override suspend fun updateRecipe(
        recipe: Recipe,
        originImgPathList: List<String>
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDatabase.withTransaction {
                    uselessImageDao.deleteUselessImage(
                        recipe.stepList
                            .map { it.imgPath }
                            .plus(recipe.imgPath),
                    )
                    uselessImageDao.insertUselessImage(
                        originImgPathList.filter { it.isNotBlank() }
                            .map { UselessImageDto(it) }
                    )
                    recipeDao.deleteStepList(recipe.recipeId)
                    recipeDao.insertRecipeMeta(recipe.toDto())
                    recipe.stepList.forEachIndexed { idx, recipeStep ->
                        recipeDao.insertRecipeStep(recipeStep.toDto(recipe.recipeId, idx + 1))
                    }
                }
            }
        }

    override suspend fun deleteRecipe(
        recipe: Recipe
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDatabase.withTransaction {
                    uselessImageDao.insertUselessImage(
                        recipe.stepList
                            .map { UselessImageDto(it.imgPath) }
                            .plus(UselessImageDto(recipe.imgPath)),
                    )
                    recipeDao.deleteRecipe(recipe.toDto())
                }
            }
        }

    override fun getRecipeFlow(
        recipeId: String
    ): Flow<Recipe> =
        recipeDao.getRecipe(recipeId)
            .map { it.toDomain() }

    override suspend fun getRecipe(
        recipeId: String
    ): Result<Recipe> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDao.getRecipeDto(recipeId)?.toDomain() ?: throw NotExistRecipeException()
            }
        }
}
