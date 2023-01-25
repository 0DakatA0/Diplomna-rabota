package org.elsys.healthmap.ui.gym

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.FragmentGymEditBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository

class GymEditFragment : Fragment() {
    private val viewModel: GymsViewModel by activityViewModels()
    private var isChanged = false
    private val args: GymEditFragmentArgs by navArgs()
    private lateinit var gym: Gym

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGymEditBinding.inflate(inflater, container, false)

        gym = viewModel.gyms.value?.get(args.id)?.let { Gym(it) }!!

        binding.gym = gym
        binding.tags = gym.tags.joinToString(", ")

        val priceTable = binding.priceTableRecyclerView

        val dataset = (gym.priceTable.toMap())
        priceTable.adapter = GymEditPriceTableAdapter(dataset)
        priceTable.setHasFixedSize(false)

        binding.description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                gym.description = s.toString()
                isChanged = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        this.lifecycleScope.launch {
            if (isChanged) {
                args.id?.let { GymsRepository.saveGym(it, gym) }
                isChanged = false
            }
        }
    }
}