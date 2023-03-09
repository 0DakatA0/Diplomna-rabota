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
    // FIXME use emptyList() for lists with no elements
    private val _gyms: MutableLiveData<List<Gym>> = MutableLiveData(listOf())
    val gyms: LiveData<List<Gym>>
        get() = _gyms


    fun getGyms(center: GeoLocation, radius: Double, filter: String) {
        GymsRepository.getGymsByLocation(center, radius) {
            if(filter.isNotEmpty()) {
                // FIXME use filter.startsWith() instead if comparing filter[0]
                // FIXME since you are filering in both cases, you can move the if inside the filter
                //   block and handle both cases:
                //  _gyms.value = it.filter { if (...) {} else {} }
                _gyms.value = if (filter[0] == '#') {
                    it.filter { gym ->
                        val tags = gym.tags
                        // FIXME you can replace the rest of the block with tags.any(...), no need to use return keyword
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