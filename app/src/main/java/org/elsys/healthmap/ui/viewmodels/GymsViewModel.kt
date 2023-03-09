package org.elsys.healthmap.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository

class GymsViewModel : ViewModel() {
    private val _gyms: MutableLiveData<Map<String, Gym>> = MutableLiveData(emptyMap())

    // FIXME that's an interesting construct. Alternatively you can call GymRepository.getGyms
    //  in an init block and have gyms = _gyms
    val gyms: LiveData<Map<String, Gym>> = liveData {
        GymsRepository.getGyms {
            _gyms.value = it
        }

        emitSource(_gyms)
    }

    // FIXME instead of exposing suspending function use viewModelScope here to do the job
    //  The VM should know how to create a new Gym
    suspend fun createGym(id: String, gym: Gym) {
//        val newGyms = _gyms.value as MutableMap<String, Gym>
//        newGyms[id] = gym
//        _gyms.value = newGyms

        GymsRepository.addGym(id, gym)
    }

    // FIXME instead of exposing suspending function use viewModelScope here to do the job
    suspend fun deleteGym(id: String) {
//        val newGyms = _gyms.value as MutableMap<String, Gym>
//        newGyms.remove(id)
//        _gyms.value = newGyms

        GymsRepository.deleteGym(id)
    }
}