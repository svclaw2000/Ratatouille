package com.kdjj.local.dto

import androidx.room.Embedded
import androidx.room.Relation

internal data class RecipeTempDto(
    @Embedded val recipeMeta: RecipeTempMetaDto,
    @Relation(
        parentColumn = "recipeTypeId",
        entityColumn = "recipeTypeId"
    )
    val recipeType: RecipeTypeDto,
    @Relation(
        parentColumn = "recipeMetaId",
        entityColumn = "parentRecipeId"
    )
    val steps: List<RecipeTempStepDto>
)
