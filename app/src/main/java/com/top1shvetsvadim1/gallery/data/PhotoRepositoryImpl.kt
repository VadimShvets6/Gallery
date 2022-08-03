package com.top1shvetsvadim1.gallery.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.top1shvetsvadim1.gallery.domain.FiltersItems
import com.top1shvetsvadim1.gallery.domain.HeaderItem
import com.top1shvetsvadim1.gallery.domain.PhotoItem
import com.top1shvetsvadim1.gallery.domain.PhotoRepository
import com.top1shvetsvadim1.gallery.presentation.utils.BitmapUtils
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel
import com.zomato.photofilters.FilterPack
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class PhotoRepositoryImpl : PhotoRepository {

    private val filterItems = arrayListOf<FiltersItems>()

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

        val list = mutableListOf<PhotoItem>()

        while (cursor.moveToNext()) {
            try {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dataId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                val id = cursor.getLong(idColumn)
                val url = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val dateModified =
                    Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dataId)))
                val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                list.add(
                    PhotoItem(url.toString(), url, dateFormat.format(dateModified))
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        cursor.close()
        val group = list.groupBy { it.data }
        return group.flatMap {
            listOf(HeaderItem(it.key, it.key, it.value.size)) + it.value.map { photo ->
                PhotoItem(
                    photo.tag,
                    photo.mediaUrl,
                    photo.data,
                    photo.isFavorite
                )
            }
        }
    }


    //----------------------------------GET BITMAP LIST -------------------------------------------

    private fun addFiltersItems(item: FiltersItems) {
        filterItems.add(item)
    }

    private fun processFilter(): ArrayList<FiltersItems> {
        filterItems.forEach { filter ->
            try {
                filter.image = filter.image?.let {
                    Log.d("BITMAP", filter.image.toString())
                    Bitmap.createScaledBitmap(
                        it,
                        it.width / PREVIEW_RATE,
                        it.height / PREVIEW_RATE,
                        false
                    )
                }
                filter.image = filter.filter.processFilter(filter.image)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }

        }
        return filterItems
    }

    private fun clearList() {
        filterItems.clear()
    }

    override suspend fun getBitmapList(uri: Uri, context: Context): List<FiltersItems> {
        val bitmap = BitmapUtils.getBitmap(
            uri,
            context
        )
        clearList()
        val filterPack = FilterPack.getFilterPack(context)
        filterPack.forEach { filter ->
            if (bitmap != null && !bitmap.isRecycled) {
                val filterItem = FiltersItems(bitmap, filter)
                addFiltersItems(filterItem)
            }
        }
        return processFilter()
    }
    //----------------------------------GET BITMAP LIST -------------------------------------------


    //-----------------------------------SHARE IMAGE------------------------------------------------

    private fun isExternalStorageAvailable() =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED


    //TODO: make it kotlin
    override fun shareImage(image: Bitmap, context: Context): Uri? {
        if (isExternalStorageAvailable()) {
            return try {
                val fileName =
                    "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}"
                val mediaFile = tempFile(fileName, context)
                val fos = FileOutputStream(mediaFile)
                image.compress(Bitmap.CompressFormat.JPEG, QUALITY_SHARE, fos)
                fos.apply {
                    flush()
                    close()
                }
               getUriFile(mediaFile, context)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
        return null
    }

    private fun getUriFile(mediaFile : File, context: Context) : Uri{
       return FileProvider.getUriForFile(
            context,
            context.packageName + PROVIDER_ENDPOINT,
            mediaFile
        )
    }

    private fun tempFile(fileName: String, context: Context) : File {
        return File.createTempFile(
            fileName,
            FILE_TYPE,
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }
    //-----------------------------------SHARE IMAGE------------------------------------------------


    //----------------------------------SAVE IMAGE TO GALLERY---------------------------------------
    //TODO: make it kotlin
    override suspend fun saveImageToGallery(
        context: Context,
        bitmap: Bitmap,
        displayName: String
    ): Uri {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }
        }
        var uri: Uri? = null
        return try {
            uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: throw IOException("Failed to create new MediaStore record.")
            context.contentResolver.openOutputStream(uri)?.use {
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_SAVE, it))
                    throw IOException("Failed to save bitmap.")
            } ?: throw IOException("Failed to open output stream.")
            uri
        } catch (e: IOException) {
            uri?.let { orphanUri ->
                context.contentResolver.delete(orphanUri, null, null)
            }
            throw e
        }
    }

    companion object {
        private const val PREVIEW_RATE = 6
        private const val FILE_TYPE = ".jpg"
        private const val PROVIDER_ENDPOINT = ".provider"
        private const val QUALITY_SHARE = 100
        private const val QUALITY_SAVE = 95
        private const val MIME_TYPE = "image/jpeg"
    }
}