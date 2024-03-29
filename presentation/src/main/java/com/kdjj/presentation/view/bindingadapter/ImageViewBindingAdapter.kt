package com.kdjj.presentation.view.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.kdjj.presentation.R

@BindingAdapter("app:loadImage", "app:defaultImage", requireAll = false)
internal fun ImageView.loadImage(src: String?, defaultResId: Int?) {
    Glide.with(context)
        .load(src ?: defaultResId ?: R.drawable.ic_my_recipe_24dp)
        .into(this)
}