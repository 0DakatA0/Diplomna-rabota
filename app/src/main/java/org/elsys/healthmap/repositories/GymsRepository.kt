package org.elsys.healthmap.repositories

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.elsys.healthmap.models.Gym

class GymsRepository {
    companion object{
        private val db = FirebaseFirestore.getInstance()
        private val auth = Firebase.auth

        fun getGyms(onSnapshotEvent: (Map<String, Gym>) -> Unit){
            Log.d("GymsRepository", "getGyms: ${auth.currentUser?.uid}")

            db.collection("gyms")
                .whereEqualTo("owner", auth.currentUser?.uid!!)
                .addSnapshotListener { snapshot, _ ->
                    val gyms = mutableMapOf<String, Gym>()

                    Log.d("GymsRepository", "getGyms: ${snapshot?.documents?.size}")

                    for(doc in snapshot!!) {
                        gyms[doc.id] = doc.toObject(Gym::class.java)
                    }

                    onSnapshotEvent(gyms)
                }
//                .get()
//                .await()
//                .toObjects(Gym::class.java)


        }

        suspend fun getGym(id: String): Gym? {
            return db.collection("gyms")
                .document(id)
                .get()
                .await()
                .toObject(Gym::class.java)
        }

        suspend fun addGym(id: String, gym: Gym) {
            gym.owner = auth.currentUser?.uid!!

            db.collection("gyms")
                .document(id)
                .set(gym)
                .await()
        }

        suspend fun saveGym(id: String, gym: Gym) {
            db.collection("gyms")
                .document(id)
                .set(gym)
                .await()
        }

        suspend fun deleteGym(id: String) {
            val gym = db.collection("gyms")
                .document(id)
                .get()
                .await()
                .toObject(Gym::class.java)

//            for (photo in gym?.photos!!) {
//                ImagesRepository.deleteImage(photo)
//            }

            db.collection("gyms")
                .document(id)
                .delete()
                .await()
        }
    }
}