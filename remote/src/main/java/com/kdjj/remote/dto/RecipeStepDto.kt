package com.kdjj.remote.dto

import com.kdjj.domain.model.RecipeStepType

internal data class RecipeStepDto(
    val stepId: String = "",
    val name: String = "",
    val type: RecipeStepType = RecipeStepType.PREPARE,
    val description: String = "",
    val imgPath: String? = null,
    val seconds: Int = 0
)
