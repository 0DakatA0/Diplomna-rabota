package org.elsys.healthmap.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.geofire.GeoLocation
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository

class UserViewModel : ViewModel() {
    private val _gyms: MutableLiveData<List<Gym>> = MutableLiveData(listOf())
    val gyms: LiveData<List<Gym>>
        get() = _gyms

    suspend fun updateGyms(center: GeoLocation, radius: Double) {
        _gyms.value = GymsRepository.getGymsByLocation(center, radius)
    }
}