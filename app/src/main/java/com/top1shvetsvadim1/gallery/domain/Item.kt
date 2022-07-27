package com.top1shvetsvadim1.gallery.domain

import android.icu.text.CaseMap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//TODO: Item should not be a sealed class, but interface. In large projects there are 100+ items, so sealed class is not a good solution.
sealed class Item {
    data class HeaderItem(val title: String, val size: Int): Item()
    data class PhotoItem(val photo : Photo) : Item()
}