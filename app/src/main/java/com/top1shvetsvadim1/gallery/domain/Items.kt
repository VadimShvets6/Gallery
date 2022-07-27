package com.top1shvetsvadim1.gallery.domain

import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel

data class HeaderItem(val title: String, val size: Int) : ItemUIModel{
    override fun areItemsTheSame(other: ItemUIModel): Boolean {
        return other is Photo && other.data == title
    }

    override fun areContentsTheSame(other: ItemUIModel): Boolean {
        return other is Photo && other.data == title
    }
}

data class PhotoItem(
    val photo: Photo
) : ItemUIModel {
    override fun areContentsTheSame(other: ItemUIModel): Boolean {
        return other is Photo && other.data == photo.data
    }

    override fun areItemsTheSame(other: ItemUIModel): Boolean {
        return other is Photo && other.mediaUrl == photo.mediaUrl
    }
}