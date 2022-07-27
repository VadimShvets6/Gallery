package com.top1shvetsvadim1.gallery.presentation.fragments.MainScreenFragment
//TODO: package name
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top1shvetsvadim1.gallery.domain.Photo
import com.top1shvetsvadim1.gallery.data.PhotoRepositoryImpl
import com.top1shvetsvadim1.gallery.domain.Item
import com.top1shvetsvadim1.gallery.domain.LoadPhotoFromGalleryUseCase
import kotlinx.coroutines.Dispatchers
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
        //TODO: do not use async/await if it is not necessary
        //TODO: launch IO operations using Dispatchers.IO
        //TODO: launch intensive operations using Dispatchers.Default
        /*viewModelScope.launch(Dispatchers.Default) {
            _listPhoto.value = loadPhotoFromGalleryUseCase(context)
        }*/
    }

    fun changeEnableState(item: Item.PhotoItem) {
        item.photo.isChecked = !item.photo.isChecked
    }
}