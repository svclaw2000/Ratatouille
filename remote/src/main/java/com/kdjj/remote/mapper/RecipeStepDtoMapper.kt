package com.kdjj.remote.mapper

import com.kdjj.domain.model.RecipeStep
import com.kdjj.remote.dto.RecipeStepDto

internal fun RecipeStepDto.toDomain(): RecipeStep =
    RecipeStep(
        stepId,
        name,
        type,
        description,
        imgPath,
        seconds
    )

internal fun RecipeStep.toDto(): RecipeStepDto =
    RecipeStepDto(
        stepId,
        name,
        type,
        description,
        imgPath,
        seconds
    )
