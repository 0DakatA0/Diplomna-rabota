package org.elsys.healthmap.ui.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository
import org.elsys.healthmap.repositories.ImagesRepository
import java.io.File
import java.io.FileOutputStream

class GymEditViewModel : ViewModel() {

    // FIXME field initialization happens in declaration order, move this declaration
    //  below the declarations of the fields that are accessed in the launch block
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

    // FIXME instead of exposing suspending function use viewModelScope here to do the job
    suspend fun saveGym() {
        // FIXME use meaningful names for all lambda params when having a nested one
        gymId?.let { _gym.value?.let { it1 -> GymsRepository.saveGym(it, it1) } }
    }

    // FIXME instead of exposing suspending function use viewModelScope here to do the job
    suspend fun addPhoto(uri: Uri, contentResolver: ContentResolver, cacheDir: File) {
        val imgName = ImagesRepository.uploadImage(uri, contentResolver, cacheDir)

        val newPhotos = _photos.value as MutableList<String>
        newPhotos.add(imgName)
        _photos.value = newPhotos
    }

    // FIXME instead of exposing suspending function use viewModelScope here to do the job
    suspend fun deletePhoto(photo: String, cacheDir: File) {
        ImagesRepository.deleteImage(photo, cacheDir)
        // FIXME prefer to use immutable collections, this cast here makes an assumption
        //  that might not be true
        val newPhotos = _photos.value as MutableList<String>
        newPhotos.remove(photo)
        _photos.value = newPhotos
    }

    fun addPriceTableElement(bundle: Bundle) {
        val newPriceTable = _priceTable.value as MutableMap<String, Float>
        // FIXME have a constant for the keys and don't use !!, verify that the input is valid instead
        newPriceTable[bundle.getString("product")!!] = bundle.getFloat("price")
        _priceTable.value = newPriceTable
        _gym.value?.priceTable = newPriceTable
    }

    fun deletePriceTableElement(product: String) {
        // FIXME prefer to use immutable collections, this cast here makes an assumption
        //  that might not be true
        val newPriceTable = _priceTable.value as MutableMap<String, Float>
        newPriceTable.remove(product)
        _priceTable.value = newPriceTable
        _gym.value?.priceTable = newPriceTable
    }
}