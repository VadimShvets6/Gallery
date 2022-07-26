package com.top1shvetsvadim1.gallery.domain

import android.content.Context

class LoadPhotoFromGalleryUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(context : Context) = repository.loadListPhoto(context)
}