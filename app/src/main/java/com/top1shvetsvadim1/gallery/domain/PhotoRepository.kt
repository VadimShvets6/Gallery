package com.top1shvetsvadim1.gallery.domain

import android.content.Context

interface PhotoRepository {

    suspend fun loadListPhoto(context : Context) : List<Item>
}