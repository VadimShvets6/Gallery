package com.top1shvetsvadim1.gallery.domain

import android.content.Context
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel

interface PhotoRepository {

    suspend fun loadListPhoto(context : Context) : List<ItemUIModel>
}