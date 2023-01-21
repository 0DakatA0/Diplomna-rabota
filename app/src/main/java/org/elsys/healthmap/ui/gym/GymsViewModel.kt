package org.elsys.healthmap.ui.gym

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import org.elsys.healthmap.models.Gym

class GymsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    val gyms: LiveData<List<Gym>> = liveData {
//        emit(
//            listOf(
//                Gym(
//                    "Gym1",
//                    emptyList(),
//                    "address1",
//                    3.5f,
//                    "This is the description description",
//                    listOf("tag1", "tag2"),
//                    mapOf(
//                        "1 month" to 10f,
//                        "3 months" to 20f,
//                        "6 months" to 30f,
//                        "1 year" to 40f
//                    )
//                ),
//                Gym(
//                    "Gym1",
//                    emptyList(),
//                    "address1",
//                    4f,
//                    "This id description",
//                    listOf("tag1", "tag2"),
//                    mapOf(
//                        "1 month" to 10f,
//                        "3 months" to 20f,
//                        "6 months" to 30f,
//                        "1 year" to 40f
//                    )
//                ),
//                Gym(
//                    "Gym1",
//                    emptyList(),
//                    "address1",
//                    5f,
//                    "This id description",
//                    listOf("tag1", "tag2"),
//                    mapOf(
//                        "1 month" to 10f,
//                        "3 months" to 20f,
//                        "6 months" to 30f,
//                        "1 year" to 40f
//                    )
//                ),
//            )
//        )
//
//        )

        val gyms = mutableListOf<Gym>()

        db.collection("gyms")
            .get()
            .await()
            .documents.forEach {
                val gym = it.toObject(Gym::class.java)
                if (gym != null) {
                    gyms += gym
                }
            }

        emit(gyms)
//            .addOnCompleteListener {
//                if(it.isSuccessful) {
//                    val gyms = it.result?.documents.forEach {
//                        val gym = it.toObject(Gym::class.java)
//                            if(gym != null) {
//                                emit(gym)
//                            }
//                    }
//                }
//            }
    }
}






