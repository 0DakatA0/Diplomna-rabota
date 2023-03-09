package org.elsys.healthmap.ui.gym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.FragmentAddPriceTableElementBinding
import org.elsys.healthmap.repositories.GymsRepository
import org.elsys.healthmap.ui.viewmodels.GymsViewModel

class AddPriceTableElementFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddPriceTableElementBinding.inflate(inflater, container, false)

        val btn = binding.addElementToPriceTable

        btn.setOnClickListener {
            val product = binding.productName.text.toString()
            val price = binding.productPrice.text.toString().toDoubleOrNull()

            if (product.isBlank()) {
                binding.productName.error = "Product name is required"
            }

            if (price == null) {
                binding.productPrice.error = "Product price is required"
                return@setOnClickListener
            }

            if (price <= 0) {
                binding.productPrice.error = "Product price must be relevant"
                return@setOnClickListener
            }

            // FIXME declare constants for all keys here
            setFragmentResult(
                "ADD_PRICE_TABLE_ELEMENT",
                bundleOf("product" to product, "price" to price)
            )
            findNavController().popBackStack()
        }

        return binding.root
    }
}