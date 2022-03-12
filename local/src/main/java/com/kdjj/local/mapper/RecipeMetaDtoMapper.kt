package com.kdjj.local.mapper

import com.kdjj.domain.model.Recipe
import com.kdjj.local.dto.RecipeMetaDto

internal fun Recipe.toDto(isTemp: Boolean): RecipeMetaDto =
    RecipeMetaDto(
        recipeMetaId = recipeId,
        title = title,
        stuff = stuff,
        imgPath = imgPath,
        authorId = authorId,
        isFavorite = isFavorite,
        createTime = createTime,
        state = state,
        recipeTypeId = type.id.toLong(),
        isTemp = isTemp
    )