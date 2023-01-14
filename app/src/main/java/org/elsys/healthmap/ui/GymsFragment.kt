package org.elsys.healthmap.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.elsys.healthmap.databinding.FragmentGymsBinding

class GymsFragment : Fragment() {

    private lateinit var binding: FragmentGymsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGymsBinding.inflate(layoutInflater)

        val gyms = listOf<Gym>(
            Gym(
                "Gym1",
                emptyList(),
                "address1",
                3f,
                "This id description",
                listOf("tag1", "tag2"),
                mapOf(
                    Pair("Product", "Price"),
                    Pair("Product", "Price")
                )
            ),
            Gym(
                "Gym1",
                emptyList(),
                "address1",
                4f,
                "This id description",
                listOf("tag1", "tag2"),
                mapOf(
                    Pair("Product", "Price"),
                    Pair("Product", "Price")
                )
            ),
            Gym(
                "Gym1",
                emptyList(),
                "address1",
                5f,
                "This id description",
                listOf("tag1", "tag2"),
                mapOf(
                    Pair("Product", "Price"),
                    Pair("Product", "Price")
                )
            ),
        )

        val recyclerView = binding.gymsRecyclerView
        val adapter = GymAdapter(requireContext(), gyms)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)

        Log.d(".GymsView", gyms.size.toString())

        return binding.root
    }
}