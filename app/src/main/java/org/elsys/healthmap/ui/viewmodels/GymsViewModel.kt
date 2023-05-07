package org.elsys.healthmap.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository

class GymsViewModel : ViewModel() {
    private val _hasFailed = MutableLiveData(false)
    val hasFailed: LiveData<Boolean>
        get() = _hasFailed

    private val _failedImagePositions: MutableLiveData<List<Int>> = MutableLiveData(listOf())
    val failedImagePositions: LiveData<List<Int>>
        get() = _failedImagePositions

    fun addFailedImage(position: Int) {
        _failedImagePositions.value = _failedImagePositions.value?.plus(position)

        if(_hasFailed.value == false){
            _hasFailed.value = true
        }
    }

    fun clearFailedImages() {
        _failedImagePositions.value = listOf()

        if(_hasFailed.value == true){
            _hasFailed.value = false
        }
    }

    private val _gyms: MutableLiveData<Map<String, Gym>> = MutableLiveData(emptyMap())
    val gyms: LiveData<Map<String, Gym>> = liveData {
        GymsRepository.getGyms {
            _gyms.value = it
        }

        emitSource(_gyms)
    }

    suspend fun createGym(id: String, gym: Gym) {
        GymsRepository.addGym(id, gym)
    }

    suspend fun deleteGym(id: String) {
        GymsRepository.deleteGym(id)
    }
}