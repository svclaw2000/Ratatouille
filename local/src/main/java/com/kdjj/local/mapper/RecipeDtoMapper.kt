package com.kdjj.local.mapper

import com.kdjj.domain.model.Recipe
import com.kdjj.local.dto.RecipeDto

internal fun RecipeDto.toDomain(): Recipe =
    Recipe(
        recipeId = recipeMeta.recipeMetaId,
        title = recipeMeta.title,
        type = recipeType.toDomain(),
        stuff = recipeMeta.stuff,
        imgHash = recipeMeta.imgHash,
        stepList = steps.map { it.toDomain() },
        authorId = recipeMeta.authorId,
        viewCount = 0,
        isFavorite = recipeMeta.isFavorite,
        createTime = recipeMeta.createTime,
        state = recipeMeta.state
    )