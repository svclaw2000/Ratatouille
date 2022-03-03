package com.kdjj.presentation.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.kdjj.presentation.model.RecipeListDto

internal class RecipeListDtoDiffCallback : DiffUtil.ItemCallback<RecipeListDto>() {

    override fun areItemsTheSame(oldItem: RecipeListDto, newItem: RecipeListDto): Boolean =
        oldItem.recipeId == newItem.recipeId

    override fun areContentsTheSame(oldItem: RecipeListDto, newItem: RecipeListDto): Boolean =
        oldItem == newItem
}