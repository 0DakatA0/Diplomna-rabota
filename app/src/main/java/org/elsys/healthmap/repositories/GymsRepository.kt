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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.elsys.healthmap.models.Gym
// FIXME use single forms instead of plurals when naming classes and packages
class GymsRepository {
    // FIXME Static access to the repository methods is generally not OK. Ideally you should use
    //  dependency injection for repository classes and inject them in the view models
    //  (see https://developer.android.com/training/dependency-injection/hilt-android if it fits the scope
    //  of the project)
    //  Usually you would have an interface GymRepository and then a concrete implementation, e.g. FirebaseGymRepository

    // FIXME in Kotlin you can declare GymsRepository as object instead of class and convert it to a static class
    //  The main advantage is that this way you can implement different interfaces, like in this sample declaration:
    //      object FirebaseGymRepository : GymRepository
    //  for more details see https://kotlinlang.org/docs/object-declarations.html#object-declarations-overview
    companion object{
        private val db = FirebaseFirestore.getInstance()
        private val auth = Firebase.auth

        fun getGyms(onSnapshotEvent: (Map<String, Gym>) -> Unit){
            Log.d("GymsRepository", "getGyms: ${auth.currentUser?.uid}")

            // FIXME it's a good practice to extract paths and field names as private constants:
            //      private const val PATH_GYMS = "gyms"
            //  This way you minimize the chance for errors due to typos and in case you need to
            //  change the path you have to fix it in just one place
            db.collection("gyms")
                .whereEqualTo("owner", auth.currentUser?.uid!!)
                .addSnapshotListener { snapshot, _ ->
                    val gyms = mutableMapOf<String, Gym>()

                    Log.d("GymsRepository", "getGyms: ${snapshot?.documents?.size}")

                    // FIXME codestyle - add a space after for (or use auto-formatting, Code -> Reformat code)
                    for(doc in snapshot!!) {
                        gyms[doc.id] = doc.toObject(Gym::class.java)
                    }

                    onSnapshotEvent(gyms)
                }


        }

        fun getGymsByLocation(center: GeoLocation, radius: Double, onUpdateData: (List<Gym>) -> Unit) {
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius)
            // FIXME you don't have to specify the val type, it will be resolved automatically if you add
            //  type info to the list instantiation:
            //      val tasks = ArrayList<Task<QuerySnapshot>>()
            val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
            // FIXME Use more meaningful variable names
            for (b in bounds) {
                val q = db.collection("gyms")
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash)

                tasks.add(q.get())
            }

            // FIXME same remark as above
            val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
            val gyms: MutableList<Gym> = mutableListOf()

            Tasks.whenAllComplete(tasks).addOnCompleteListener { results ->
                results.isSuccessful
                // FIXME this is not OK, TResult.result can throw an exception if the task failed
                //  what you should do here is to first check if results.isSuccessful is true
                //  and only then use results.result
                //  You should avoid using the !! operator, unless you are 100% certain you are applying
                //  it to a non-null value. In this case you can go with:
                //      results?.result ?: emptyList()
                for (task in results.result!!){
                    val snap = task.result as QuerySnapshot
                    for (doc in snap) {
                        // FIXME Don't proceed if you don't have valid coordinates
                        //   you can do this:
                        //      val coordinates  = doc.getGeoPoint("coordinates") ?: continue
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

                // FIXME When you have nested lambdas it's best to add
                //  meaningful names for all lambda parameters
                matchingDocs.forEach {
                    it.toObject(Gym::class.java)?.let { it1 -> gyms.add(it1) }
                }

                onUpdateData(gyms)
            }
        }

        // FIXME since fetching data is a network operation, it's recommended to use the IO dispatcher for that:
        //      suspend fun getGym(id: String): Gym? = withContext(ioDispatcher) {
        //  (you'll have to remove the return keyword for this to work)
        //  Note that I've not written Dispatchers.IO - this is on purpose, in real world scenarios
        //  you don't want to hardcode the dispatcher and it would be passed when constructing the repository.
        //  For the scope of this project using Dispatchers.IO might be OK
        //  This comment is applicable to all suspending functions in the project that deal with network and
        //  file access
        suspend fun getGym(id: String): Gym? {
            return db.collection("gyms")
                .document(id)
                .get()
                .await()
                .toObject(Gym::class.java)
        }

        suspend fun addGym(id: String, gym: Gym) {
            // FIXME Don't use !!, handle missing user gracefully,
            //  you might make the function return false on error for example
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