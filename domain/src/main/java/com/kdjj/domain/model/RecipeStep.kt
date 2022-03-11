package com.kdjj.domain.model

data class RecipeStep(
    val stepId: String,
    val name: String,
    val type: RecipeStepType,
    val description: String,
    val imgHash: String?,
    val seconds: Int
)
