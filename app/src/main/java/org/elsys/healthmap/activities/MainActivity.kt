package org.elsys.healthmap.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ContextCompat.getSystemService(
            this,
            LocationManager::class.java
        )!!
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                2
            )
        }

        super.onCreate(savedInstanceState)
        val auth = Firebase.auth

        if (auth.currentUser == null) {
            startActivity(Intent(this@MainActivity, UserActivity::class.java))
        } else {
            startActivity(Intent(this@MainActivity, GymOwnerActivity::class.java))
        }

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        applicationContext.cacheDir.listFiles()?.forEach {
            it.delete()
        }
    }
}