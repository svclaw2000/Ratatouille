package com.kdjj.remote.mapper

import com.kdjj.domain.model.RecipeType
import com.kdjj.remote.dto.RecipeTypeDto

internal fun RecipeTypeDto.toDomain(): RecipeType =
    RecipeType(
        id,
        title
    )

internal fun RecipeType.toDto(): RecipeTypeDto =
    RecipeTypeDto(
        id,
        title
    )
