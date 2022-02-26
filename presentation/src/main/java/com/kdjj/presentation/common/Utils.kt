package com.kdjj.presentation.common

import com.kdjj.domain.model.Recipe

internal const val RECIPE_ID = "recipeID"
internal const val RECIPE_STATE = "recipeState"
internal const val UPDATED_RECIPE_ID = "updatedRecipeId"
internal const val ACTION_START = "ACTION_START"

internal fun calculateSeconds(min: Int, sec: Int): Int {
    return min * 60 + sec
}

internal fun calculateTotalTime(recipe: Recipe?): Int {
    return recipe?.let { it.stepList.map { it.seconds }.fold(0) { acc, i -> acc + i } } ?: 0
}
