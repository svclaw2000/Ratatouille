package com.kdjj.local.mapper

import com.kdjj.domain.model.RecipeStep
import com.kdjj.local.dto.RecipeStepDto

internal fun RecipeStepDto.toDomain() =
    RecipeStep(
        stepId = stepId,
        name = name,
        type = type,
        description = description,
        imgPath = imgPath,
        seconds = seconds
    )

internal fun RecipeStep.toDto(recipeMetaID: String, order: Int, isTemp: Boolean): RecipeStepDto =
    RecipeStepDto(
        stepId = stepId,
        name = name,
        order = order,
        type = type,
        description = description,
        imgPath = imgPath,
        seconds = seconds,
        parentRecipeId = recipeMetaID,
        isTemp = isTemp
    )
