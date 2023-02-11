package org.elsys.healthmap.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository

class GymsViewModel : ViewModel() {
    private val _gyms: MutableLiveData<Map<String, Gym>> = MutableLiveData(emptyMap())


    val gyms: LiveData<Map<String, Gym>> = liveData {
        GymsRepository.getGyms {
            _gyms.value = it
        }

        emitSource(_gyms)
    }

    suspend fun createGym(id: String, gym: Gym) {
//        val newGyms = _gyms.value as MutableMap<String, Gym>
//        newGyms[id] = gym
//        _gyms.value = newGyms

        GymsRepository.addGym(id, gym)
    }

    suspend fun deleteGym(id: String) {
//        val newGyms = _gyms.value as MutableMap<String, Gym>
//        newGyms.remove(id)
//        _gyms.value = newGyms

        GymsRepository.deleteGym(id)
    }
}