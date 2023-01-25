package org.elsys.healthmap.ui.gym

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository

class GymsViewModel : ViewModel() {
    val gyms: LiveData<Map<String, Gym>> = liveData {
        val gyms = GymsRepository.getGyms()
        emit(gyms)
    }
}






