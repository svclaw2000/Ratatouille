package com.kdjj.presentation.model

import com.kdjj.domain.model.RecipeState

internal data class RecipeListDto(
    val recipeId: String,
    val title: String,
    val totalTime: Int,
    val stuff: String,
    val viewCount: Int,
    val imgHash: String?,
    val createTime: Long,
    val state: RecipeState,
)
