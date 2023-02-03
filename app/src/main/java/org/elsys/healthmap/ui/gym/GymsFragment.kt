package org.elsys.healthmap.ui.gym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.FragmentGymsBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository

class GymsFragment : Fragment() {
    private val viewModel: GymsViewModel by activityViewModels()

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
                val id = GymsRepository.addGym(gym)
                viewModel.addGym(gym, id)
                val action = GymsFragmentDirections.actionGymsFragmentToGymEditFragment(id)
                findNavController().navigate(action)
            }
        }

        val recyclerView = binding.gymsRecyclerView
        val adapter = GymAdapter(gyms, requireContext(), viewModel)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)

        gyms.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }
}