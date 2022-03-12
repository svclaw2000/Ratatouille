package com.kdjj.presentation.model

import com.kdjj.domain.model.Recipe
import com.kdjj.presentation.common.FILE_PATH

internal sealed class MyRecipeItem {

    data class MyRecipe(
        val recipe: Recipe
    ) : MyRecipeItem() {
        val imgPath = recipe.imgHash?.let { "${FILE_PATH}/${it}" }
    }

    object PlusButton : MyRecipeItem()
    object Progress: MyRecipeItem()
}