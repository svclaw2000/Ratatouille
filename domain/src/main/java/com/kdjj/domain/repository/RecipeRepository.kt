package com.kdjj.domain.repository

import com.kdjj.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    suspend fun saveMyRecipe(
        recipe: Recipe
    ): Result<Unit>

    suspend fun updateMyRecipe(
        recipe: Recipe
    ): Result<Unit>

    suspend fun deleteMyRecipe(
        recipe: Recipe
    ): Result<Unit>

    suspend fun uploadRecipe(
        recipe: Recipe
    ): Result<Unit>

    suspend fun increaseOthersRecipeViewCount(
        recipe: Recipe
    ): Result<Unit>

    suspend fun deleteOthersRecipe(
        recipe: Recipe
    ): Result<Unit>

    suspend fun fetchOthersRecipe(
        recipeId: String
    ): Result<Recipe>

    fun getRecipeUpdateFlow(): Flow<Unit>

    suspend fun getLocalRecipe(
        recipeId: String
    ): Result<Recipe>

    suspend fun updateMyRecipe(
        recipe: Recipe,
        originImgPathList: List<String>
    ): Result<Unit>
}
