package com.kdjj.local.dataSource

import com.kdjj.domain.model.Recipe
import com.kdjj.local.model.RecipeTypeEntity

interface LocalDataSource {

    suspend fun saveRecipe(recipe: Recipe)

    suspend fun saveRecipeTypes()

    suspend fun getRecipeTypes(): List<RecipeTypeEntity>
}