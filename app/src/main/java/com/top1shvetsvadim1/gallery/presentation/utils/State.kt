package com.top1shvetsvadim1.gallery.presentation.utils

sealed class State() {
    data class Loading(val isLoading: Boolean = false) : State()
}

