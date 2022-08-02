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
                val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault());
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
            listOf(HeaderItem(it.key, it.value.size)) + it.value.map { photo ->
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
                        it.width / 2, //TODO: add config or const variable that has name, f.e it.width * FilterConfig.PreviewRate
                        it.height / 2,
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
            if (bitmap != null) { //TODO: && !bitmap.isRecycled
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
            val mediaStorageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp"
            val fileType = ".jpg"

            val mediaFile: File
            try {
                mediaFile = File.createTempFile(fileName, fileType, mediaStorageDir)
                val fos = FileOutputStream(mediaFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush()
                fos.close()

                return FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    mediaFile
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
    //-----------------------------------SHARE IMAGE------------------------------------------------


    //----------------------------------SAVE IMAGE TO GALLERY---------------------------------------
    //TODO: make it kotlin
    override fun saveImageToGallery(
        context: Context,
        bitmap: Bitmap,
        displayName: String
    ): Uri {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }
        }

        val resolver = context.contentResolver
        var uri: Uri? = null

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")

            resolver.openOutputStream(uri)?.use {
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it))
                    throw IOException("Failed to save bitmap.")
            } ?: throw IOException("Failed to open output stream.")

            return uri

        } catch (e: IOException) {
            uri?.let { orphanUri ->
                resolver.delete(orphanUri, null, null)
            }
            throw e
        }
    }
}