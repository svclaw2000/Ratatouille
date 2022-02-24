package com.kdjj.local.mapper

import com.kdjj.domain.model.RecipeType
import com.kdjj.local.dto.RecipeTypeDto

internal fun RecipeTypeDto.toDomain(): RecipeType =
    RecipeType(
        recipeTypeId.toInt(),
        title
    )

internal fun RecipeType.toDto(): RecipeTypeDto =
    RecipeTypeDto(
        id.toLong(),
        title
    )
