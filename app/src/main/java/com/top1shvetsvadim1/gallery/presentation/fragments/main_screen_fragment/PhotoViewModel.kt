package com.top1shvetsvadim1.gallery.presentation.fragments.main_screen_fragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top1shvetsvadim1.gallery.data.PhotoRepositoryImpl
import com.top1shvetsvadim1.gallery.domain.LoadPhotoFromGalleryUseCase
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel
import com.top1shvetsvadim1.gallery.presentation.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {

    private val repository = PhotoRepositoryImpl()
    private val loadPhotoFromGalleryUseCase = LoadPhotoFromGalleryUseCase(repository)

    private val _listPhoto = MutableLiveData<List<ItemUIModel>>()
    val listPhoto: LiveData<List<ItemUIModel>>
        get() = _listPhoto

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun getListPhoto(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            showLoading()
            val result = loadPhotoFromGalleryUseCase(context)
            _listPhoto.postValue(result)
            hideLoading()
        }
    }

    private fun showLoading() {
        _state.postValue(State.Loading(true))
    }

    private fun hideLoading() {
        _state.postValue(State.Loading(false))
    }
}