package org.elsys.healthmap.ui.gym

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import org.elsys.healthmap.R
import org.elsys.healthmap.activities.AuthenticationActivity
import org.elsys.healthmap.databinding.FragmentGymsBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.ui.viewmodels.GymsViewModel
import org.elsys.healthmap.ui.viewmodels.ImageFailureViewModel
import java.util.*

class GymsFragment : Fragment() {
    private val viewModel: GymsViewModel by viewModels()
    private val imageFailureViewModel: ImageFailureViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGymsBinding.inflate(inflater, container, false)
        val gyms = viewModel.gyms
        val addGymButton = binding.addGymButton

        addGymButton.setOnClickListener {
            viewModel.viewModelScope.launch {
                val gym = Gym()
                val id = UUID.randomUUID().toString()
                viewModel.createGym(id, gym)
                val action = GymsFragmentDirections.actionGymsFragmentToGymEditFragment(id)
                findNavController().navigate(action)
            }
        }

        val recyclerView = binding.gymsRecyclerView
        val adapter = GymAdapter(
            gyms,
            requireContext().cacheDir,
            viewModel.viewModelScope,
            {
                imageFailureViewModel.addFailedImage(it)
            }
        ) {
            val builder = AlertDialog.Builder(context)

            builder.apply {
                setPositiveButton("Yes") { _, _ ->
                    viewModel.viewModelScope.launch {
                        viewModel.deleteGym(it)
                    }
                }
                setNegativeButton("No") { _, _ ->

                }
            }
            builder.setMessage("Are you shore that you want to delete that gym?")
            builder.create().show()
        }
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)

        gyms.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        imageFailureViewModel.hasImageDownloadFailed.observe(viewLifecycleOwner) {
            if (it) {
                val floatingActionButton = requireActivity().findViewById<View>(R.id.addGymButton)
                imageFailureViewModel.showSnackBarMessage(
                    requireView(),
                    floatingActionButton,
                    adapter
                )
            }
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLogOut -> {
                Firebase.auth.signOut()
                startActivity(Intent(context, AuthenticationActivity::class.java))
                true
            }

            else -> false
        }
    }
}