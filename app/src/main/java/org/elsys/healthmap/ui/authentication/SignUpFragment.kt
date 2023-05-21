package org.elsys.healthmap.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.elsys.healthmap.activities.GymOwnerActivity
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
            val confirmPassword = binding.signUpConfirmPassword.text.toString()

            if(email.isEmpty()) {
                binding.signUpEmail.error = "Email is required"
                return@setOnClickListener
            }

            if(password.isEmpty()) {
                binding.signUpPassword.error = "Password is required"
                return@setOnClickListener
            }

            if(password.length < 6) {
                binding.signUpPassword.error = "Password must be at least 6 characters long"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.signUpConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                if(this@SignUpFragment.lifecycle.currentState == Lifecycle.State.RESUMED){
                    startActivity(Intent(context, GymOwnerActivity::class.java))
                }
            }
        }

        return binding.root
    }
}