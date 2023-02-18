package org.elsys.healthmap.repositories

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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


        }

        fun getGymsByLocation(center: GeoLocation, radius: Double, onUpdateData: (List<Gym>) -> Unit) {
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius)
            val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
            for (b in bounds) {
                val q = db.collection("gyms")
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash)

                tasks.add(q.get())
            }

            val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
            val gyms: MutableList<Gym> = mutableListOf()

            Tasks.whenAllComplete(tasks).addOnCompleteListener { results ->
                for (task in results.result!!){
                    val snap = task.result as QuerySnapshot
                    for (doc in snap) {
                        val coordinates  = doc.getGeoPoint("coordinates")

                        val lat = coordinates?.latitude!!
                        val lng = coordinates?.longitude!!

                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radius) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                matchingDocs.forEach {
                    it.toObject(Gym::class.java)?.let { it1 -> gyms.add(it1) }
                }

                onUpdateData(gyms)
            }
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