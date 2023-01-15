package org.elsys.healthmap.ui.gym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.elsys.healthmap.databinding.FragmentGymsBinding

class GymsFragment : Fragment() {
    private val viewModel: GymsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGymsBinding.inflate(layoutInflater)

        val gyms = viewModel.gyms

        val recyclerView = binding.gymsRecyclerView
        val adapter = GymAdapter(gyms)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)

        gyms.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }
}