package com.top1shvetsvadim1.gallery.domain

import android.content.Context
import android.net.Uri

class GetListBitmapUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(uri: Uri, context: Context) = repository.getBitmapList(uri, context)
}