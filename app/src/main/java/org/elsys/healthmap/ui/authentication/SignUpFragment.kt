package org.elsys.healthmap.ui.authentication

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
import org.elsys.healthmap.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)

        val auth = Firebase.auth
        val button = binding.signUpButton

        button.setOnClickListener {
            val email = binding.signUpEmail.text.toString()
            val password = binding.signUpPassword.text.toString()

            lifecycleScope.launch {
                val signUp = auth.createUserWithEmailAndPassword(email, password).await()

                if(signUp.user != null) {
                    findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToGymsFragment())
                }
                else {
                    Log.d("SignUp", "Failed to sign up")
                }
            }
        }

        return binding.root
    }
}