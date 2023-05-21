package org.elsys.healthmap.ui.viewmodels

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository
import org.elsys.healthmap.repositories.ImagesRepository
import java.io.File

class GymEditViewModel : ViewModel() {
    var gymId: String? = null
        set(value) {
            if(value == field) return

            viewModelScope.launch {
                val gymFromDB = value?.let { GymsRepository.getGym(it) }
                _gym.value = gymFromDB

                if (gymFromDB != null) {
                    _priceTable.value = gymFromDB.priceTable
                    _photos.value = gymFromDB.photos
                }
            }
            field = value
        }

    private val _gym = MutableLiveData(Gym())
    val gym: LiveData<Gym>
        get() = _gym

    private val _photos = MutableLiveData<List<String>>(emptyList())
    val photos: LiveData<List<String>>
        get() = _photos

    private val _priceTable = MutableLiveData<Map<String, Float>>(emptyMap())
    val priceTable: LiveData<Map<String, Float>>
        get() = _priceTable

    private val _isUploadImageSuccessful = MutableLiveData(true)
    val isUploadImageSuccessful: LiveData<Boolean>
        get() = _isUploadImageSuccessful

    private fun setIsUploadImageSuccessful(value: Boolean) {
        if(value != _isUploadImageSuccessful.value) {
            _isUploadImageSuccessful.value = value
        }
    }

    suspend fun saveGym() {
        gymId?.let { _gym.value?.let { it1 -> GymsRepository.saveGym(it, it1) } }
    }

    suspend fun addPhoto(uri: Uri, contentResolver: ContentResolver, cacheDir: File) {
        try {
            setIsUploadImageSuccessful(true)

            val imgName = ImagesRepository.uploadImage(uri, contentResolver, cacheDir)

            val newPhotos = _photos.value as MutableList<String>
            newPhotos.add(imgName)
            _photos.value = newPhotos
        } catch (e: Exception) {
            setIsUploadImageSuccessful(false)
        }
    }

    suspend fun deletePhoto(photo: String) {
        ImagesRepository.deleteImage(photo)

        val newPhotos = _photos.value as MutableList<String>
        newPhotos.remove(photo)
        _photos.value = newPhotos
    }

    fun addPriceTableElement(bundle: Bundle) {
        val newPriceTable = _priceTable.value as MutableMap<String, Float>
        Log.d("GymEditViewModel", bundle.getFloat("price").toString())
        newPriceTable[bundle.getString("product")!!] = bundle.getFloat("price")
        _priceTable.value = newPriceTable
        _gym.value?.priceTable = newPriceTable
    }

    fun deletePriceTableElement(product: String) {
        val newPriceTable = _priceTable.value as MutableMap<String, Float>
        newPriceTable.remove(product)
        _priceTable.value = newPriceTable
        _gym.value?.priceTable = newPriceTable
    }
}