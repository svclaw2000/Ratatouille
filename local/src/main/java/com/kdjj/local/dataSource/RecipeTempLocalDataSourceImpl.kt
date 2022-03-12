package com.kdjj.local.dataSource

import androidx.room.withTransaction
import com.kdjj.data.datasource.RecipeTempLocalDataSource
import com.kdjj.domain.model.Recipe
import com.kdjj.local.dao.RecipeDao
import com.kdjj.local.dao.UselessImageDao
import com.kdjj.local.database.RecipeDatabase
import com.kdjj.local.dto.UselessImageDto
import com.kdjj.local.mapper.toDomain
import com.kdjj.local.mapper.toDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RecipeTempLocalDataSourceImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val recipeDatabase: RecipeDatabase,
    private val uselessImageDao: UselessImageDao
) : RecipeTempLocalDataSource {

    override suspend fun saveRecipeTemp(recipe: Recipe): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDatabase.withTransaction {
                    uselessImageDao.deleteUselessImage(
                        recipe.stepList
                            .map { it.imgPath }
                            .plus(recipe.imgPath)
                            .filterNotNull()
                    )

                    removeImageByRecipeId(recipe.recipeId)

                    recipeDao.deleteTempStepList(recipe.recipeId)
                    recipeDao.insertRecipeMeta(recipe.toDto(isTemp = true))
                    recipe.stepList.forEachIndexed { index, recipeStep ->
                        recipeDao.insertRecipeStep(recipeStep.toDto(
                            recipeMetaID = recipe.recipeId,
                            order = index + 1,
                            isTemp = true
                        ))
                    }
                }
            }
        }

    override suspend fun deleteRecipeTemp(recipeId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDatabase.withTransaction {
                    removeImageByRecipeId(recipeId)
                    recipeDao.deleteRecipeTemp(recipeId)
                }
            }
        }

    override suspend fun getRecipeTemp(recipeId: String): Result<Recipe?> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDao.getRecipeTemp(recipeId)?.toDomain()
            }
        }

    private suspend fun removeImageByRecipeId(recipeId: String) {
        recipeDao.getRecipeTemp(recipeId)
            ?.toDomain()
            ?.let { temp ->
                uselessImageDao.insertUselessImage(
                    temp.stepList
                        .map { it.imgPath }
                        .plus(temp.imgPath)
                        .filterNotNull()
                        .map { UselessImageDto(it) }
                )
            }
    }
}