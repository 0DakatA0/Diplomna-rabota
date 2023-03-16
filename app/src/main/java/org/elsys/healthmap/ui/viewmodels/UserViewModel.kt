package org.elsys.healthmap.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.geofire.GeoLocation
import kotlinx.coroutines.launch
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository

class UserViewModel : ViewModel() {
    private val _gyms: MutableLiveData<List<Gym>> = MutableLiveData(listOf())
    val gyms: LiveData<List<Gym>>
        get() = _gyms


    fun updateGyms(center: GeoLocation, radius: Double, filter: String) {
        GymsRepository.getGymsByLocation(center, radius) {
            if(filter.isNotEmpty()) {
                _gyms.value = if (filter[0] == '#') {
                    it.filter { gym ->
                        val tags = gym.tags
                        tags.forEach { tag ->
                            if (tag.contains(filter.substring(1))) return@filter true
                        }

                        return@filter false
                    }
                } else {
                    it.filter { gym ->
                        return@filter gym.name.contains(filter)
                    }
                }
            } else {
                _gyms.value = it
            }
        }
    }
}