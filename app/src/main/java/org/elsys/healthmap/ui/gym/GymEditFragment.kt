package org.elsys.healthmap.ui.gym

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import org.elsys.healthmap.R
import org.elsys.healthmap.databinding.FragmentGymEditBinding
import org.elsys.healthmap.ui.viewmodels.GymEditViewModel
import org.elsys.healthmap.ui.viewmodels.ImageFailureViewModel

class GymEditFragment : Fragment() {
    private val viewModel: GymEditViewModel by viewModels()
    private val imageFailureViewModel: ImageFailureViewModel by viewModels()
    private var isChanged = false
    private val args: GymEditFragmentArgs by navArgs()
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                lifecycleScope.launch {
                    viewModel.addPhoto(
                        uri,
                        requireContext().contentResolver,
                        requireContext().cacheDir
                    )
                    isChanged = true
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGymEditBinding.inflate(inflater, container, false)
        viewModel.gymId = args.id

        val adapter = GymImagesAdapter(
            viewModel.photos,
            requireContext().cacheDir,
            viewModel.viewModelScope,
            {
                imageFailureViewModel.addFailedImage(it)
            }
        ) {
            viewModel.viewModelScope.launch {
                viewModel.deletePhoto(it)
            }
        }

        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        val gymImagesRecyclerView = binding.gymImagesRecyclerView
        gymImagesRecyclerView.adapter = adapter
        gymImagesRecyclerView.setHasFixedSize(false)

        viewModel.gym.observe(viewLifecycleOwner) {
            binding.gym = viewModel.gym.value
            binding.tags = viewModel.gym.value?.tags?.joinToString(", ")
        }

        binding.addressPickerNavigationButton.setOnClickListener {
            val action = GymEditFragmentDirections.actionGymEditFragmentToPickAddressFragment()
            findNavController().navigate(action)
        }

        setFragmentResultListener("PICK_ADDRESS") { _, bundle ->
            val lat = bundle.getDouble("latitude")
            val lng = bundle.getDouble("longitude")
            val address = bundle.getString("address")

            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lng))

            if (address != null) {
                viewModel.gym.value?.address = address
            }
            viewModel.gym.value?.geohash = hash
            viewModel.gym.value?.coordinates = GeoPoint(lat, lng)
            isChanged = true
        }

        val priceTable = binding.priceTableRecyclerView
        val priceTableDataset = viewModel.priceTable
        priceTable.adapter = GymEditPriceTableAdapter(priceTableDataset) { product ->
            viewModel.deletePriceTableElement(product)
        }

        viewModel.priceTable.observe(viewLifecycleOwner) {
            priceTable.adapter?.notifyDataSetChanged()
        }

        priceTable.setHasFixedSize(false)

        binding.addPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.gymName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.gym.value?.name = s.toString()
                isChanged = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.tagsField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.gym.value?.tags = s.toString().split(", ") as MutableList<String>
                isChanged = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.gym.value?.description = s.toString()
                isChanged = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.addToPriceTableButton.setOnClickListener {
            val action =
                GymEditFragmentDirections.actionGymEditFragmentToAddPriceTableElementFragment()
            findNavController().navigate(action)
        }

        setFragmentResultListener("ADD_PRICE_TABLE_ELEMENT") { _, bundle ->
            viewModel.addPriceTableElement(bundle)
        }

        viewModel.isUploadImageSuccessful.observe(viewLifecycleOwner) {
            if(it == false) {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        imageFailureViewModel.hasImageDownloadFailed.observe(viewLifecycleOwner) {
            if (it) {
                imageFailureViewModel.showSnackBarMessage(
                    binding.root,
                    binding.root,
                    adapter
                )
            }
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()

        viewModel.viewModelScope.launch {
            if (isChanged) {
                viewModel.saveGym()
                isChanged = false
            }
        }
    }

}