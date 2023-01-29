package org.elsys.healthmap.repositories

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

class ImagesRepository {
    companion object {
        private val storageRef = Firebase.storage.reference

        suspend fun getImages(images: List<String>) {
            images.forEach { image ->
                val imageRef = storageRef.child(image)
                val localFile = File.createTempFile(image, "jpeg")
                imageRef.getFile(localFile).await()
            }
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