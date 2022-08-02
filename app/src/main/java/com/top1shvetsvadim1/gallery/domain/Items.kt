package com.top1shvetsvadim1.gallery.domain

import android.net.Uri
import android.os.Parcelable
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel
import kotlinx.parcelize.Parcelize

data class HeaderItem(val title: String, val size: Int) : ItemUIModel {
    override fun areItemsTheSame(other: ItemUIModel): Boolean {
        return other is HeaderItem && other.title == title
    }

    override fun areContentsTheSame(other: ItemUIModel): Boolean {
        return other is HeaderItem && other.title == title
    }

    override fun changePayload(other: ItemUIModel): Any {
        return false
    }
}

@Parcelize
data class PhotoItem(
    val tag: String,
    val mediaUrl: Uri,
    val data: String,
    var isFavorite: Boolean = false
) : ItemUIModel, Parcelable {
    override fun areItemsTheSame(other: ItemUIModel): Boolean {
        return other is PhotoItem && other.tag == tag
    }

    override fun areContentsTheSame(other: ItemUIModel): Boolean {
        return other is PhotoItem && other.isFavorite == isFavorite && other.mediaUrl == mediaUrl
    }

    override fun changePayload(other: ItemUIModel): Any {
        return if (other is PhotoItem) {
            other.isFavorite != isFavorite
        } else false
    }
}