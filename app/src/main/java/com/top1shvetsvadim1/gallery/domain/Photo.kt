package com.top1shvetsvadim1.gallery.domain

import android.net.Uri
import android.os.Parcelable
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Photo(
   // val tag : Int,
    val mediaUrl : Uri,
    val data : String,
    var isChecked : Boolean = false
) : Parcelable //: ItemUIModel{
//    override fun areItemsTheSame(other: ItemUIModel): Boolean {
//        return other is Photo && other.tag == tag
//    }
//
//    override fun areContentsTheSame(other: ItemUIModel): Boolean {
//        return other is Photo && other.mediaUrl == mediaUrl
//    }
//}
