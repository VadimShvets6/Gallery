package com.top1shvetsvadim1.gallery.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel

interface PhotoRepository {

    suspend fun loadListPhoto(context: Context): List<ItemUIModel>

    suspend fun getBitmapList(uri: Uri, context: Context): List<FiltersItems>

    fun shareImage(image: Bitmap, context: Context): Uri?

    suspend fun saveImageToGallery(context: Context, bitmap: Bitmap, displayName: String): Uri

}