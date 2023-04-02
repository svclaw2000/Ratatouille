package com.kdjj.local.dataSource

import androidx.room.withTransaction
import com.kdjj.data.datasource.RecipeLocalDataSource
import com.kdjj.domain.model.Recipe
import com.kdjj.domain.model.exception.NotExistRecipeException
import com.kdjj.local.dao.RecipeDao
import com.kdjj.local.dao.RecipeImageDao
import com.kdjj.local.database.RecipeDatabase
import com.kdjj.local.dto.UselessImageDto
import com.kdjj.local.mapper.toDomain
import com.kdjj.local.mapper.toDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RecipeLocalDataSourceImpl @Inject constructor(
    private val recipeDatabase: RecipeDatabase,
    private val recipeDao: RecipeDao,
    private val recipeImageDao: RecipeImageDao
) : RecipeLocalDataSource {

    override suspend fun saveRecipe(
        recipe: Recipe
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDatabase.withTransaction {
                    recipeImageDao.deleteUselessImage(
                        recipe.stepList
                            .map { it.imgPath }
                            .plus(recipe.imgPath)
                            .filterNotNull()
                    )
                    recipeDao.deleteStepList(recipe.recipeId)
                    recipeDao.insertRecipeMeta(recipe.toDto(isTemp = false))
                    recipe.stepList.forEachIndexed { idx, recipeStep ->
                        recipeDao.insertRecipeStep(recipeStep.toDto(recipe.recipeId, idx + 1, isTemp = false))
                    }
                }
            }
        }

    override suspend fun updateRecipe(
        recipe: Recipe
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDao.updateRecipeMeta(recipe.toDto(isTemp = false))
            }
        }

    override suspend fun updateRecipe(
        recipe: Recipe,
        originImgPathList: List<String>
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDatabase.withTransaction {
                    recipeImageDao.insertUselessImage(
                        originImgPathList.filter { it.isNotBlank() }
                            .map { UselessImageDto(it) }
                    )
                    recipeImageDao.deleteUselessImage(
                        recipe.stepList
                            .map { it.imgPath }
                            .plus(recipe.imgPath)
                            .filterNotNull(),
                    )
                    recipeDao.deleteStepList(recipe.recipeId)
                    recipeDao.insertRecipeMeta(recipe.toDto(isTemp = false))
                    recipe.stepList.forEachIndexed { idx, recipeStep ->
                        recipeDao.insertRecipeStep(recipeStep.toDto(recipe.recipeId, idx + 1, isTemp = false))
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
                    recipeImageDao.insertUselessImage(
                        recipe.stepList
                            .map { it.imgPath }
                            .plus(recipe.imgPath)
                            .filterNotNull()
                            .map { UselessImageDto(it) }
                    )
                    recipeDao.deleteRecipe(recipe.recipeId)
                }
            }
        }

    override suspend fun getRecipe(
        recipeId: String
    ): Result<Recipe> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDao.getRecipeDto(recipeId, isTemp = false)?.toDomain() ?: throw NotExistRecipeException()
            }
        }
}
