package org.elsys.healthmap.repositories

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class ImagesRepository {
    companion object {
        private val storageRef = Firebase.storage.reference

        suspend fun getImage(image: String, downloadFile: File) {
            val imageRef = storageRef.child(image)

            imageRef.getFile(downloadFile).await()
        }

        suspend fun uploadImage(image: Uri): String {
            val imageName = UUID.randomUUID().toString()
            val imageRef = storageRef.child(imageName)

            imageRef.putFile(image).await()
            return imageName
        }

        fun deleteImage(image: String) {

        }
    }
}