package com.kdjj.local.mapper

import com.kdjj.domain.model.RecipeStep
import com.kdjj.local.dto.RecipeStepDto

internal fun RecipeStepDto.toDomain() =
    RecipeStep(
        stepId,
        name,
        type,
        description,
        imgHash,
        seconds
    )

internal fun RecipeStep.toDto(recipeMetaID: String, order: Int): RecipeStepDto =
    RecipeStepDto(
        stepId,
        name,
        order,
        type,
        description,
        imgHash,
        seconds,
        recipeMetaID
    )
