package com.kdjj.local.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RecipeType")
internal data class RecipeTypeDto(
    @PrimaryKey
    val recipeTypeId: Long,
    val title: String
) {
    
    companion object {
        
        val defaultTypes = mutableListOf(
            RecipeTypeDto(1, "기타"),
            RecipeTypeDto(2, "한식"),
            RecipeTypeDto(3, "중식"),
            RecipeTypeDto(4, "양식"),
            RecipeTypeDto(5, "일식"),
        )
    }
}
