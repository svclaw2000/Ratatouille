package com.kdjj.local.dto

import androidx.room.DatabaseView

@DatabaseView("SELECT imgPath FROM RecipeMeta WHERE imgPath IS NOT NULL " +
        "UNION SELECT imgPath FROM RecipeStep WHERE imgPath IS NOT NULL")
data class RecipeImageViewDto(
    val imgPath: String
)