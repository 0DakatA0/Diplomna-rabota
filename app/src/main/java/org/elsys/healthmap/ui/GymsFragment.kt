package org.elsys.healthmap.ui

import android.os.Bundle
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

        return binding.root
    }
}