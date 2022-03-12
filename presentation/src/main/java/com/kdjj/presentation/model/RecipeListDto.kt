package com.kdjj.presentation.model

import com.kdjj.domain.model.RecipeState
import com.kdjj.presentation.common.FILE_PATH

internal data class RecipeListDto(
    val recipeId: String,
    val title: String,
    val totalTime: Int,
    val stuff: String,
    val viewCount: Int,
    val imgHash: String?,
    val createTime: Long,
    val state: RecipeState,
) {
    val imgPath = imgHash?.let {
        when (state) {
            RecipeState.NETWORK -> "https://firebasestorage.googleapis.com/v0/b/ratatouille-e3c15.appspot.com/o/images%2F${it}.png?alt=media"
            else -> "${FILE_PATH}/${it}.png"
        }
    }
}
