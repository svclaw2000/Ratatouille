package com.kdjj.presentation.mapper

import com.kdjj.domain.model.Recipe
import com.kdjj.presentation.model.RecipeListDto

internal fun Recipe.toRecipeListDto() =
    RecipeListDto(
        recipeId,
        title,
        stepList.map { it.seconds }.fold(0) { acc, i -> acc + i },
        stuff,
        viewCount,
        imgHash,
        createTime,
        state
    )
