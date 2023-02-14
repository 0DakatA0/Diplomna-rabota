package org.elsys.healthmap.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.elsys.healthmap.activities.GymOwnerActivity
import org.elsys.healthmap.activities.UserActivity
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

        var role = ""

        val roles = arrayOf("Regular User", "Gym Owner")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)

        val dropdown = binding.dropdownMenu
        dropdown.adapter = adapter

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as String

                role = when (selectedItem) {
                    "Regular User" -> "regular"
                    "Gym Owner" -> "gymOwner"
                    else -> "regular"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                role = "regular"
            }
        }

        button.setOnClickListener {
            val email = binding.signUpEmail.text.toString()
            val password = binding.signUpPassword.text.toString()

            lifecycleScope.launch {
                val signUp = auth.createUserWithEmailAndPassword(email, password).await()

                if (signUp.user != null) {
                    val intent = when(role){
                        "gymOwner" -> Intent(requireContext(), GymOwnerActivity::class.java)
                        else -> Intent(requireContext(), UserActivity::class.java)
                    }

                    val user = Firebase.auth.currentUser?.getIdToken(true)?.await()
                    if (user != null) {
                        user.claims["role"] = role
                    }

                    if (user != null) {
                        Log.d("SignUp", "Signed up as ${user.claims}")
                    }

                    startActivity(intent)
                } else {
                    Log.d("SignUp", "Failed to sign up")
                }
            }
        }

        return binding.root
    }
}