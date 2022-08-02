package com.top1shvetsvadim1.gallery.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import java.io.InputStream
import kotlin.math.sqrt


object BitmapUtils {

    private const val IMAGE_MAX_SIZE = 1200000

    fun getBitmap(uri: Uri, context: Context): Bitmap? {
        val inputStream: InputStream?
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            var resultBitmap =
                BitmapFactory.decodeStream(inputStream, null, BitmapFactory.Options())
            val y =
                sqrt(IMAGE_MAX_SIZE / (getSize(resultBitmap?.width) / getSize(resultBitmap?.height)))
            val x = (y / getSize(resultBitmap?.height)) * getSize(resultBitmap?.width)
            if (resultBitmap != null) {
                resultBitmap = Bitmap.createScaledBitmap(resultBitmap, x.toInt(), y.toInt(), true)
                System.gc()
            } else {
                resultBitmap = BitmapFactory.decodeStream(inputStream)
            }
            inputStream?.close()
            return rotateImageIfRequired(context, resultBitmap, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getSize(size: Int?): Double {
        return size?.toDouble() ?: 0.0
    }


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