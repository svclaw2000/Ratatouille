package com.kdjj.local.mapper

import com.kdjj.domain.model.RecipeStep
import com.kdjj.local.dto.RecipeTempStepDto

internal fun RecipeTempStepDto.toDomain() =
    RecipeStep(
        stepId,
        name,
        type,
        description,
        imgHash,
        seconds
    )

internal fun RecipeStep.toTempDto(recipeMetaID: String, order: Int): RecipeTempStepDto =
    RecipeTempStepDto(
        stepId,
        name,
        order,
        type,
        description,
        imgHash,
        seconds,
        recipeMetaID
    )
