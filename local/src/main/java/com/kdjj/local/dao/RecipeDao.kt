package com.kdjj.local.dao

import androidx.room.*
import com.kdjj.local.dto.RecipeDto
import com.kdjj.local.dto.RecipeMetaDto
import com.kdjj.local.dto.RecipeStepDto
import com.kdjj.local.dto.RecipeTypeDto

@Dao
internal interface RecipeDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeMeta(recipeMeta: RecipeMetaDto)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeStep(recipeStep: RecipeStepDto)
    
    @Query("DELETE FROM RecipeStep WHERE parentRecipeId = :recipeId AND isTemp = 0")
    suspend fun deleteStepList(recipeId: String)
    
    @Query("DELETE FROM RecipeMeta WHERE recipeMetaId = :recipeId")
    suspend fun deleteRecipe(recipeId: String)
    
    @Update
    suspend fun updateRecipeMeta(recipeMeta: RecipeMetaDto)

    @Query("SELECT * FROM RecipeType WHERE recipeTypeId = :typeId")
    suspend fun getRecipeTypeDto(typeId: Long): RecipeTypeDto

    @Query("SELECT * FROM RecipeMeta WHERE recipeMetaId = :recipeId AND isTemp = :isTemp")
    suspend fun getRecipeMetaDto(recipeId: String, isTemp: Boolean): RecipeMetaDto?

    @Query("SELECT * FROM RecipeStep WHERE parentRecipeId = :recipeId AND isTemp = :isTemp")
    suspend fun getRecipeStepDtoList(recipeId: String, isTemp: Boolean): List<RecipeStepDto>

    @Transaction
    suspend fun getRecipeDto(recipeId: String, isTemp: Boolean): RecipeDto? {
        val meta = getRecipeMetaDto(recipeId, isTemp) ?: return null
        val type = getRecipeTypeDto(meta.recipeTypeId)
        val steps = getRecipeStepDtoList(recipeId, isTemp)
        return RecipeDto(meta, type, steps)
    }

    @Query("DELETE FROM RecipeMeta WHERE recipeMetaId = :recipeId AND isTemp = 1")
    suspend fun deleteRecipeTemp(recipeId: String)

    @Query("DELETE FROM RecipeStep WHERE parentRecipeId = :recipeId AND isTemp = 1")
    suspend fun deleteTempStepList(recipeId: String)
}
