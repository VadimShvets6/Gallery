package com.top1shvetsvadim1.gallery.presentation.fragments.main_screen_fragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top1shvetsvadim1.gallery.data.PhotoRepositoryImpl
import com.top1shvetsvadim1.gallery.domain.LoadPhotoFromGalleryUseCase
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {

    private val repository = PhotoRepositoryImpl()
    private val loadPhotoFromGalleryUseCase = LoadPhotoFromGalleryUseCase(repository)

    private val _listPhoto = MutableLiveData<List<ItemUIModel>>()
    val listPhoto: LiveData<List<ItemUIModel>>
        get() = _listPhoto

    fun getListPhoto(context: Context) {
        //TODO: do not use async/await if it is not necessary
        //TODO: launch IO operations using Dispatchers.IO
        //TODO: launch intensive operations using Dispatchers.Default
        viewModelScope.launch(Dispatchers.Default) {
            val result = loadPhotoFromGalleryUseCase(context)
            _listPhoto.postValue(result)
        }
    }
}