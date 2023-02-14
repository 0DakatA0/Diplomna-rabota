package org.elsys.healthmap.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = Firebase.auth

        if (auth.currentUser == null) {
            startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java))
        } else {
            lifecycleScope.launch {
                val intent = when (auth.currentUser!!.getIdToken(true).await().claims["role"] as String?) {
                    "gymOwner" -> Intent(this@MainActivity, GymOwnerActivity::class.java)
                    else -> Intent(this@MainActivity, UserActivity::class.java)
                }

                startActivity(intent)
            }
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