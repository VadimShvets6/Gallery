package com.top1shvetsvadim1.gallery.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.top1shvetsvadim1.gallery.domain.Item
import com.top1shvetsvadim1.gallery.domain.Photo
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel

object PhotoDiffCallback : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem //oldItem.areItemsTheSame(newItem)
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return  oldItem == newItem //oldItem.areContentsTheSame(newItem)
    }
}