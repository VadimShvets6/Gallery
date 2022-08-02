package com.top1shvetsvadim1.gallery.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import java.io.BufferedInputStream
import java.io.InputStream
import kotlin.math.sqrt


object BitmapUtils {

    private const val IMAGE_MAX_SIZE = 1200000

    private val options get() = BitmapFactory.Options()

    fun getBitmap(uri: Uri, context: Context): Bitmap? {
        var inputStream: InputStream? = null
        return try {
            inputStream = context.contentResolver.openInputStream(uri) ?: return null
            BitmapFactory.decodeStream(inputStream, null, options)?.let { resultBitmap ->
                val y =
                    sqrt(IMAGE_MAX_SIZE / resultBitmap.width.toSize() / resultBitmap.height.toSize())
                val x = (y / resultBitmap.height.toSize()) * resultBitmap.width.toSize()
                rotateImageIfRequired(
                    context,
                    Bitmap.createScaledBitmap(resultBitmap, x.toInt(), y.toInt(), true),
                    uri
                )
            } ?: BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
            //TODO: check trash-box icon in profiler
            System.gc()
        }
    }

    private fun Int.toSize() = this.toDouble()


    //Rotate the image to the right orientation only if it was rotate 90, 180 or 270 degree.
    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap? {
        val input: InputStream? = context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface? = input?.let { ExifInterface(it) }
        return when (ei?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }
}