package com.kdjj.remote.mapper

import com.kdjj.domain.model.Recipe
import com.kdjj.domain.model.RecipeState
import com.kdjj.remote.dto.RecipeDto

internal fun RecipeDto.toDomain(): Recipe =
    Recipe(
        recipeId,
        title,
        type.toDomain(),
        stuff,
        imgPath,
        stepList.map { it.toDomain() },
        authorId,
        viewCount,
        false,
        createTime,
        RecipeState.NETWORK
    )

internal fun Recipe.toDto(): RecipeDto =
    RecipeDto(
        recipeId,
        title,
        type.toDto(),
        stuff,
        imgPath,
        stepList.map { it.toDto() },
        authorId,
        viewCount,
        createTime,
    )
