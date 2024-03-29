package com.kdjj.local.dataSource

import androidx.room.withTransaction
import com.kdjj.data.datasource.RecipeTempLocalDataSource
import com.kdjj.domain.model.Recipe
import com.kdjj.local.dao.RecipeDao
import com.kdjj.local.dao.RecipeImageDao
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
    private val recipeImageDao: RecipeImageDao
) : RecipeTempLocalDataSource {

    override suspend fun saveRecipeTemp(recipe: Recipe): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDatabase.withTransaction {
                    removeTempImageByRecipeId(recipe.recipeId)
                    recipeImageDao.deleteUselessImage(
                        recipe.stepList
                            .map { it.imgPath }
                            .plus(recipe.imgPath)
                            .filterNotNull()
                    )

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
                    removeTempImageByRecipeId(recipeId, exceptReal = true)
                    recipeDao.deleteRecipeTemp(recipeId)
                }
            }
        }

    override suspend fun getRecipeTemp(recipeId: String): Result<Recipe?> =
        withContext(Dispatchers.IO) {
            runCatching {
                recipeDao.getRecipeDto(recipeId, isTemp = true)?.toDomain()
            }
        }

    private suspend fun removeTempImageByRecipeId(recipeId: String, exceptReal: Boolean = false) {
        recipeDao.getRecipeDto(recipeId, isTemp = true)
            ?.toDomain()
            ?.let { temp ->
                recipeImageDao.insertUselessImage(
                    temp.stepList
                        .map { it.imgPath }
                        .plus(temp.imgPath)
                        .filterNotNull()
                        .map { UselessImageDto(it) }
                )
                if (exceptReal) {
                    recipeDao.getRecipeDto(recipeId, isTemp = false)
                        ?.toDomain()
                        ?.let { recipe ->
                            val imgPathList = recipe.stepList.map { it.imgPath } + recipe.imgPath
                            recipeImageDao.deleteUselessImage(imgPathList.filterNotNull())
                        }
                }
            }
    }
}