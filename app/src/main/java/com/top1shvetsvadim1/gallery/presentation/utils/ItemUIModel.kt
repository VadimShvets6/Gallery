package com.top1shvetsvadim1.gallery.presentation.utils

interface ItemUIModel {

    fun areItemsTheSame(other: ItemUIModel): Boolean

    fun areContentsTheSame(other: ItemUIModel): Boolean

    fun changePayload(other: ItemUIModel): Any
}