package com.kdjj.remote.dto

import com.kdjj.domain.model.RecipeStepType

internal data class RecipeStepDto(
    val stepId: String,
    val name: String,
    val type: RecipeStepType,
    val description: String,
    val imgPath: String?,
    val seconds: Int
)
