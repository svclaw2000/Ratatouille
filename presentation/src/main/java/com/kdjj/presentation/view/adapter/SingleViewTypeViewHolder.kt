package com.kdjj.presentation.view.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

internal class SingleViewTypeViewHolder<VDB : ViewDataBinding> constructor(
    val binding: VDB,
    onViewHolderInit: (VDB, getAdapterPosition: () -> Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        onViewHolderInit.invoke(binding, { bindingAdapterPosition })
    }
}