package com.top1shvetsvadim1.gallery.domain

import android.content.Context
import android.graphics.Bitmap

class ShareImageUseCase(
    private val repository: PhotoRepository
) {
    operator fun invoke(image: Bitmap, context: Context) = repository.shareImage(image, context)
}