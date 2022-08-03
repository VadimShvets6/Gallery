package com.top1shvetsvadim1.gallery.presentation.utils

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper

class MediaContentObserver(val onChange: () -> Unit) :
    ContentObserver(Handler(Looper.myLooper()!!)) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        onChange()
    }

    companion object {
        fun lazyInit(onChange: () -> Unit) = lazy {
            MediaContentObserver(onChange)
        }
    }
}