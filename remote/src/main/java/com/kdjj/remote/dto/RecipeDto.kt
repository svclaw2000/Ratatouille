package com.kdjj.remote.dto

internal data class RecipeDto(
    val recipeId: String,
    val title: String,
    val type: RecipeTypeDto,
    val stuff: String,
    val imgPath: String?,
    val stepList: List<RecipeStepDto>,
    val authorId: String,
    val viewCount: Int,
    val createTime: Long,
)
