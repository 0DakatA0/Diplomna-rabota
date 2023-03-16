package org.elsys.healthmap.repositories

import android.content.ContentResolver
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ImagesRepository {
    companion object {
        private val storageRef = Firebase.storage.reference

        suspend fun getImage(image: String, downloadFile: File) {
            val imageRef = storageRef.child(image)

            imageRef.getFile(downloadFile).await()
        }

        suspend fun uploadImage(uri: Uri, contentResolver: ContentResolver, cacheDir: File): String {
            val imageName = UUID.randomUUID().toString()
            val imageRef = storageRef.child(imageName)

            withContext(Dispatchers.IO) {
                val inputStream = contentResolver.openInputStream(uri)
                val file = File(cacheDir, imageName)
                val outputStream = FileOutputStream(file)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            }

            imageRef.putFile(uri).await()
            return imageName
        }

        suspend fun deleteImage(image: String) {
            val imageRef = storageRef.child(image)

            imageRef.delete().await()
        }
    }
}