package com.top1shvetsvadim1.gallery.domain

import android.content.Context
import android.graphics.Bitmap

class SaveImageToGalleryUseCase(
    private val repository: PhotoRepository
) {
    operator fun invoke(
        context: Context,
        bitmap: Bitmap,
        displayName: String
    ) = repository.saveImageToGallery(context, bitmap, displayName)
}