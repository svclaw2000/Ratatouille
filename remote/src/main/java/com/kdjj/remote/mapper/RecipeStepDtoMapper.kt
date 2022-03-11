package com.kdjj.remote.mapper

import com.kdjj.domain.model.RecipeStep
import com.kdjj.remote.dto.RecipeStepDto

internal fun RecipeStepDto.toDomain(): RecipeStep =
    RecipeStep(
        stepId = stepId,
        name = name,
        type = type,
        description = description,
        imgHash = when {
            imgPath == "" -> null
            imgPath == null -> null
            imgPath.startsWith("gs") || imgPath.startsWith("http") ->
                imgPath.substringAfter("images%2F").substringBefore(".png")
            else -> imgPath
        },
        seconds = seconds
    )

internal fun RecipeStep.toDto(): RecipeStepDto =
    RecipeStepDto(
        stepId = stepId,
        name = name,
        type = type,
        description = description,
        imgPath = imgHash,
        seconds = seconds
    )
