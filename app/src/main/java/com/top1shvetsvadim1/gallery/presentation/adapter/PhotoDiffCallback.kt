package com.top1shvetsvadim1.gallery.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel

object PhotoDiffCallback : DiffUtil.ItemCallback<ItemUIModel>() {

    override fun areItemsTheSame(oldItem: ItemUIModel, newItem: ItemUIModel): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }

    override fun areContentsTheSame(oldItem: ItemUIModel, newItem: ItemUIModel): Boolean {
        return  oldItem.areContentsTheSame(newItem)
    }
}