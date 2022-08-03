package com.top1shvetsvadim1.gallery.presentation.adapter.detail_screen_adapter

import androidx.recyclerview.widget.DiffUtil
import com.top1shvetsvadim1.gallery.domain.FiltersItems

object FilterDiffCallback : DiffUtil.ItemCallback<FiltersItems>() {
    override fun areItemsTheSame(oldItem: FiltersItems, newItem: FiltersItems): Boolean {
        return oldItem.filter == newItem.filter
    }

    override fun areContentsTheSame(oldItem: FiltersItems, newItem: FiltersItems): Boolean {
        return oldItem == newItem
    }
}