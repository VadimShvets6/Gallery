package com.top1shvetsvadim1.gallery.presentation.utils

import androidx.recyclerview.widget.GridLayoutManager

class SpanGridLayoutManager(val spanManager: (position: Int) -> Int) :
    GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return spanManager(position)
    }

    companion object {
        fun lazyInit(spanManager: (position : Int) -> Int) = lazy {
            SpanGridLayoutManager(spanManager)
        }
    }
}