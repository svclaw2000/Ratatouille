package com.kdjj.local.mapper

import com.kdjj.domain.model.Recipe
import com.kdjj.local.dto.RecipeMetaDto

internal fun Recipe.toDto(): RecipeMetaDto =
    RecipeMetaDto(
        recipeId,
        title,
        stuff,
        imgPath,
        authorId,
        isFavorite,
        createTime,
        state,
        type.id.toLong()
    )