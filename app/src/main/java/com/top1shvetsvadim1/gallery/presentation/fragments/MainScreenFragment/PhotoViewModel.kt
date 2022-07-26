package com.top1shvetsvadim1.gallery.presentation.fragments.MainScreenFragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top1shvetsvadim1.gallery.domain.Photo
import com.top1shvetsvadim1.gallery.data.PhotoRepositoryImpl
import com.top1shvetsvadim1.gallery.domain.Item
import com.top1shvetsvadim1.gallery.domain.LoadPhotoFromGalleryUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {

    private val repository = PhotoRepositoryImpl()
    private val loadPhotoFromGalleryUseCase = LoadPhotoFromGalleryUseCase(repository)

    private val _listPhoto = MutableLiveData<List<Item>>()
    val listPhoto: LiveData<List<Item>>
        get() = _listPhoto

    fun getListPhoto(context: Context) {
        val deferred = viewModelScope.async {
            val result = loadPhotoFromGalleryUseCase(context)
            result
        }
        viewModelScope.launch {
            _listPhoto.value = deferred.await()
        }
    }

    fun changeEnableState(item: Item.PhotoItem) {
        item.photo.isChecked = !item.photo.isChecked
    }
}