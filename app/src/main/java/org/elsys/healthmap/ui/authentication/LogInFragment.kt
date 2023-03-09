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
import com.google.firebase.ktx.app
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.elsys.healthmap.activities.GymOwnerActivity
import org.elsys.healthmap.activities.UserActivity
import org.elsys.healthmap.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {
    // FIXME this var should be nullable and set to null in onDestroyView(), otherwise might
    //  leak the context used to create the views stored in the binding
    private lateinit var binding: FragmentLogInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)

        binding.logInButton.setOnClickListener {
            // FIXME you might want to have a ViewModel to handle the login logic
            //  What's the purpose of having a coroutine here?
            lifecycleScope.launch {
                val auth = Firebase.auth
                val email = binding.loginUsernameEmail.text.toString()
                val password = binding.logInPassword.text.toString()

                if (email.isBlank()) {
                    binding.loginUsernameEmail.error = "Email is required"
                    return@launch
                }

                if(password.isBlank()) {
                    binding.logInPassword.error = "Password is required"
                    return@launch
                }

                // FIXME The fragment/activity might have been removed when the onSuccess/onFailure
                //  listeners get invoked, so passing context and binding directly in the callbacks
                //  might lead to leaks. One way to avoid this is to have a member val MutableLiveData<Result>
                //  that you can update with success/error result. Then you can observe this livedata
                //  safely and take the appropriate action on update
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    startActivity(Intent(context, GymOwnerActivity::class.java))
                }.addOnFailureListener {
                    if(it.message!!.contains("password")) {
                        binding.logInPassword.error = "Wrong password"
                    } else {
                        binding.loginUsernameEmail.error = "Wrong email"
                    }
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