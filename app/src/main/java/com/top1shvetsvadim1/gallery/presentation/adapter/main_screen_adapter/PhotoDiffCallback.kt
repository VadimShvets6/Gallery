package com.top1shvetsvadim1.gallery.presentation.adapter.main_screen_adapter

import androidx.recyclerview.widget.DiffUtil
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel

object PhotoDiffCallback : DiffUtil.ItemCallback<ItemUIModel>() {

    override fun areItemsTheSame(oldItem: ItemUIModel, newItem: ItemUIModel): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }

    override fun areContentsTheSame(oldItem: ItemUIModel, newItem: ItemUIModel): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }

    override fun getChangePayload(oldItem: ItemUIModel, newItem: ItemUIModel): Any {
        return oldItem.changePayload(newItem)
    }
}