package com.kdjj.remote.mapper

import com.kdjj.domain.model.Recipe
import com.kdjj.domain.model.RecipeState
import com.kdjj.remote.dto.RecipeDto

internal fun RecipeDto.toDomain(): Recipe =
    Recipe(
        recipeId = recipeId,
        title = title,
        type = type.toDomain(),
        stuff = stuff,
        imgHash = when {
            imgPath == "" -> null
            imgPath == null -> null
            imgPath.startsWith("gs") || imgPath.startsWith("http") ->
                imgPath.substringAfter("images%2F").substringBefore(".png")
            else -> imgPath
        },
        stepList = stepList.map { it.toDomain() },
        authorId = authorId,
        viewCount = viewCount,
        isFavorite = false,
        createTime = createTime,
        state = RecipeState.NETWORK
    )

internal fun Recipe.toDto(): RecipeDto =
    RecipeDto(
        recipeId = recipeId,
        title = title,
        type = type.toDto(),
        stuff = stuff,
        imgPath = imgHash,
        stepList = stepList.map { it.toDto() },
        authorId = authorId,
        viewCount = viewCount,
        createTime = createTime,
    )
