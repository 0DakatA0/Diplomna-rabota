package org.elsys.healthmap.ui.gym

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository

class GymsViewModel : ViewModel() {
    private val _gyms: MutableLiveData<Map<String, Gym>> = MutableLiveData(emptyMap())

    val gyms: LiveData<Map<String, Gym>> = liveData {
        _gyms.value = GymsRepository.getGyms()
        emitSource(_gyms)
    }

    fun saveGym(id: String, gym: Gym) {
        val newGyms = _gyms.value as MutableMap<String, Gym>
        Log.d(".GymsViewModel", (newGyms === _gyms).toString())
        newGyms[id] = gym
        _gyms.value = newGyms
    }
}






