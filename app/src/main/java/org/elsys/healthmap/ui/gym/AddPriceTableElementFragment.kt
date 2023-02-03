package org.elsys.healthmap.ui.gym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.FragmentAddPriceTableElementBinding
import org.elsys.healthmap.repositories.GymsRepository

class AddPriceTableElementFragment : Fragment() {
    private val viewModel: GymsViewModel by activityViewModels()
    private val args: GymEditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddPriceTableElementBinding.inflate(inflater, container, false)

        val btn = binding.addElementToPriceTable

        btn.setOnClickListener {
            val gym = viewModel.gyms.value?.get(args.id)
            val product = binding.productName.text.toString()
            val price = binding.productPrice.text.toString()

            if (gym != null) {
                gym.priceTable[product] = price.toFloat()
                args.id?.let { it1 -> viewModel.saveGym(it1, gym) }

                viewModel.viewModelScope.launch {
                    args.id?.let { it1 -> GymsRepository.saveGym(it1, gym) }
                }

                findNavController().popBackStack()
            }
        }

        return binding.root
    }
}