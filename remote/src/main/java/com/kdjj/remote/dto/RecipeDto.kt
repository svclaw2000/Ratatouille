package com.kdjj.remote.dto

internal data class RecipeDto(
    val recipeId: String = "",
    val title: String = "",
    val type: RecipeTypeDto = RecipeTypeDto(0, ""),
    val stuff: String = "",
    val imgPath: String = "",
    val stepList: List<RecipeStepDto> = listOf(),
    val authorId: String = "",
    val viewCount: Int = 0,
    val createTime: Long = 0L,
)
