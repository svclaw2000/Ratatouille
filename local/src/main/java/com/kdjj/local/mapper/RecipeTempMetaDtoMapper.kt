package com.kdjj.local.mapper

import com.kdjj.domain.model.Recipe
import com.kdjj.local.dto.RecipeTempMetaDto

internal fun Recipe.toTempDto(): RecipeTempMetaDto =
    RecipeTempMetaDto(
        recipeId,
        title,
        stuff,
        imgHash,
        authorId,
        isFavorite,
        createTime,
        state,
        type.id.toLong()
    )
