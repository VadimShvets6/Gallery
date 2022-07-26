package com.top1shvetsvadim1.gallery.domain

import android.icu.text.CaseMap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Item {
    data class HeaderItem(val title: String, val size: Int): Item()
    data class PhotoItem(val photo : Photo) : Item()
}