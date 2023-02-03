package org.elsys.healthmap.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.elsys.healthmap.models.Gym

class GymsRepository {
    companion object{
        private val db = FirebaseFirestore.getInstance()

        suspend fun getGyms(): Map<String, Gym>{
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

            return gyms
        }

        suspend fun saveGym(id: String, gym: Gym) {
            db.collection("gyms")
                .document(id)
                .set(gym)
                .await()
        }

        suspend fun addGym(gym: Gym): String {
            val docRef = db.collection("gyms")
                .add(gym)
                .await()

            return docRef.id
        }

        suspend fun deleteGym(id: String) {
            db.collection("gyms")
                .document(id)
                .delete()
                .await()
        }
    }
}