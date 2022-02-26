package com.kdjj.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.kdjj.presentation.databinding.ItemListRecipeBinding
import com.kdjj.presentation.model.RecipeListDto
import com.kdjj.presentation.viewmodel.home.others.OthersViewModel

internal class OthersRecipeListAdapter(
    private val viewModel: OthersViewModel,
) : SingleViewTypeListAdapter<RecipeListDto, ItemListRecipeBinding>(RecipeListItemModelDiffCallback()) {

    override fun createBinding(parent: ViewGroup): ItemListRecipeBinding =
        ItemListRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun initViewHolder(
        binding: ItemListRecipeBinding,
        getItemPosition: () -> Int,
    ) {
        binding.root.setOnClickListener {
            viewModel.recipeItemClick(getItem(getItemPosition()))
        }
    }

    override fun bind(
        holder: SingleViewTypeViewHolder<ItemListRecipeBinding>,
        item: RecipeListDto,
    ) {
       with(holder.binding) {
           recipe = item
           executePendingBindings()
       }
    }

    override fun onViewRecycled(holder: SingleViewTypeViewHolder<ItemListRecipeBinding>) {
        with(holder.binding) {
            Glide.with(root.context).clear(imageViewOthersItemImg)
        }
    }
}

internal class RecipeListItemModelDiffCallback : DiffUtil.ItemCallback<RecipeListDto>() {

    override fun areItemsTheSame(oldItem: RecipeListDto, newItem: RecipeListDto): Boolean =
        oldItem.recipeId == newItem.recipeId

    override fun areContentsTheSame(oldItem: RecipeListDto, newItem: RecipeListDto): Boolean =
        oldItem == newItem
}
