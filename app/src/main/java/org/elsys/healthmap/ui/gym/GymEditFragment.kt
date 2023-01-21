package org.elsys.healthmap.ui.gym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import org.elsys.healthmap.databinding.FragmentGymEditBinding

class GymEditFragment : Fragment() {
    private val viewModel: GymsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGymEditBinding.inflate(inflater)
        val args: GymEditFragmentArgs by navArgs()

        val gym = viewModel.gyms.value?.get(args.position)

        binding.gym = gym
        binding.tags = gym?.tags?.joinToString(", ")

        val priceTable = binding.priceTableRecyclerView

        val dataset = (gym?.priceTable?.toMap() ?: emptyMap())
        priceTable.adapter = GymEditPriceTableAdapter(dataset)
        priceTable.setHasFixedSize(false)

        return binding.root
    }
}