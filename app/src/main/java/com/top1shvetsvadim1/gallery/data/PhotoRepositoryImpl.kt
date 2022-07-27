package com.top1shvetsvadim1.gallery.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.top1shvetsvadim1.gallery.domain.*
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PhotoRepositoryImpl : PhotoRepository {

    override suspend fun loadListPhoto(context: Context): List<ItemUIModel> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        val sorted = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sorted
        )

        cursor ?: return emptyList()

        val list = mutableListOf<Photo>()

        while (cursor.moveToNext()) {
            try {
                //val tag = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dataId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                val id = cursor.getLong(idColumn)
                val url = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val dateModified =
                    Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dataId)))
                val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault());
                list.add(
                    Photo( url, dateFormat.format(dateModified))
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        cursor.close()
        val group = list.groupBy { it.data }
        return group.flatMap {
            listOf(HeaderItem(it.key, it.value.size)) + it.value.map { photo -> PhotoItem(photo) } }
    }
}