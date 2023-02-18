package org.elsys.healthmap.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.elsys.healthmap.activities.GymOwnerActivity
import org.elsys.healthmap.activities.UserActivity
import org.elsys.healthmap.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {
    private lateinit var binding: FragmentLogInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)

        binding.logInButton.setOnClickListener {
            lifecycleScope.launch {
                val auth = Firebase.auth
                val email = binding.loginUsernameEmail.text.toString()
                val password = binding.logInPassword.text.toString()

                val loggedIn = auth.signInWithEmailAndPassword(email, password).await()

                if (loggedIn.user != null) {
                    val intent = Intent(context, GymOwnerActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("LogIn", "Failed to log in")
                }
            }
        }

        binding.goToUserActivityButton.setOnClickListener {
            val intent = Intent(context, UserActivity::class.java)
            startActivity(intent)
        }

        binding.goToSignUpText.setOnClickListener {
            val action = LogInFragmentDirections.actionLogInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }
}