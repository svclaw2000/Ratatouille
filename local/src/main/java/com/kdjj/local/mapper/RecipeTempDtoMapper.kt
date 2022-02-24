package com.kdjj.local.mapper

import com.kdjj.domain.model.Recipe
import com.kdjj.local.dto.RecipeTempDto

internal fun RecipeTempDto.toDomain(): Recipe =
    Recipe(
        recipeId = recipeMeta.recipeMetaId,
        title = recipeMeta.title,
        type = recipeType.toDomain(),
        stuff = recipeMeta.stuff,
        imgPath = recipeMeta.imgPath,
        stepList = steps.map { it.toDomain() },
        authorId = recipeMeta.authorId,
        viewCount = 0,
        isFavorite = recipeMeta.isFavorite,
        createTime = recipeMeta.createTime,
        state = recipeMeta.state
    )

