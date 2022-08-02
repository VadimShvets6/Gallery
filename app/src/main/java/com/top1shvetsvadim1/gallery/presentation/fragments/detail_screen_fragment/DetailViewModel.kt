package com.top1shvetsvadim1.gallery.presentation.fragments.detail_screen_fragment

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top1shvetsvadim1.gallery.data.PhotoRepositoryImpl
import com.top1shvetsvadim1.gallery.domain.FiltersItems
import com.top1shvetsvadim1.gallery.domain.GetListBitmapUseCase
import com.top1shvetsvadim1.gallery.domain.SaveImageToGalleryUseCase
import com.top1shvetsvadim1.gallery.domain.ShareImageUseCase
import com.top1shvetsvadim1.gallery.presentation.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val repository = PhotoRepositoryImpl()
    private val getListBitmapUseCase = GetListBitmapUseCase(repository)
    private val shareImageUseCase = ShareImageUseCase(repository)
    private val saveImageToGalleryUseCase = SaveImageToGalleryUseCase(repository)

    private val _listBitmaps = MutableLiveData<List<FiltersItems>>()
    val listBitmaps: LiveData<List<FiltersItems>>
        get() = _listBitmaps

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun getListBitmaps(uri: Uri, context: Context) {
        Log.d("BITMAP", "Uri: $uri")
        viewModelScope.launch(Dispatchers.IO) {
            _state.postValue(State.Loading(true))
            val result = getListBitmapUseCase(uri, context)
            _listBitmaps.postValue(result)
            _state.postValue(State.Loading(false))
        }
    }

    fun shareImage(image: Bitmap, context: Context): Uri? {
        return shareImageUseCase(image, context)
    }

    //TODO: do save in background tread. You can use suspend modifier and launch it with coroutine to wait for result
    fun saveImageToGallery(
        context: Context,
        bitmap: Bitmap,
        displayName: String
    ): Uri {
        return saveImageToGalleryUseCase(context, bitmap, displayName)
    }
}