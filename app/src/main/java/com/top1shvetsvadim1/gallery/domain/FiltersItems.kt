package com.top1shvetsvadim1.gallery.domain

import android.graphics.Bitmap
import com.zomato.photofilters.imageprocessors.Filter

data class FiltersItems(
    var image: Bitmap?,
    val filter: Filter
)
