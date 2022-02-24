package com.kdjj.local.dto

import androidx.room.Embedded
import androidx.room.Relation

internal data class RecipeDto(
    @Embedded val recipeMeta: RecipeMetaDto,
    @Relation(
        parentColumn = "recipeTypeId",
        entityColumn = "recipeTypeId"
    )
    val recipeType: RecipeTypeDto,
    @Relation(
        parentColumn = "recipeMetaId",
        entityColumn = "parentRecipeId"
    )
    val steps: List<RecipeStepDto>
)