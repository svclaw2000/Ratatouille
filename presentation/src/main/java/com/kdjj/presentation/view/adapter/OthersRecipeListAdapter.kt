package com.kdjj.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.kdjj.domain.model.Recipe
import com.kdjj.presentation.common.calculateTotalTime
import com.kdjj.presentation.common.calculateUpdateTime
import com.kdjj.presentation.databinding.ItemOthersRecipeBinding
import com.kdjj.presentation.model.OthersRecipeModel

class OthersRecipeListAdapter(
) : SingleViewTypeListAdapter<OthersRecipeModel, ItemOthersRecipeBinding>(OthersRecipeModelDiffCallback()) {

    override fun createBinding(parent: ViewGroup): ItemOthersRecipeBinding =
        ItemOthersRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun initViewHolder(
        binding: ItemOthersRecipeBinding,
        getItemPosition: () -> Int,
    ) {
        binding.root.setOnClickListener {
            //todo: clicklisntener
        }
    }

    override fun bind(
        holder: SingleViewTypeViewHolder<OthersRecipeModel, ItemOthersRecipeBinding>,
        item: OthersRecipeModel,
    ) {
       with(holder.binding) {
           recipe = item
           executePendingBindings()
       }
    }
}

class OthersRecipeModelDiffCallback : DiffUtil.ItemCallback<OthersRecipeModel>() {

    override fun areItemsTheSame(oldItem: OthersRecipeModel, newItem: OthersRecipeModel): Boolean =
        oldItem.recipeId == newItem.recipeId

    override fun areContentsTheSame(oldItem: OthersRecipeModel, newItem: OthersRecipeModel): Boolean =
        true
}
