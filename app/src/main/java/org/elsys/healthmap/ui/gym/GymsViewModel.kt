package org.elsys.healthmap.ui.gym

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.elsys.healthmap.models.Gym

class GymsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    val gyms: LiveData<Map<String, Gym>> = liveData {
        val gyms = mutableMapOf<String, Gym>()

        db.collection("gyms")
            .get()
            .await()
            .documents.forEach {
                val gymId = it.id
                val gymData = it.toObject(Gym::class.java)

                if (gymData != null) {
                    gyms[gymId] = gymData
                }
            }

        emit(gyms)
    }

    suspend fun saveGym(id: String) {
        val gym = gyms.value?.get(id)

        if (gym != null) {
            db.collection("gyms")
                .document(id)
                .set(gym)
                .await()
        }
    }
}






