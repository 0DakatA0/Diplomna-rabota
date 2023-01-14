package org.elsys.healthmap.ui.gym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.elsys.healthmap.databinding.FragmentGymEditBinding

class GymEditFragment : Fragment() {
    private lateinit var binding: FragmentGymEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGymEditBinding.inflate(inflater)

        return binding.root
    }
}