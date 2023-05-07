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
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.launch
import org.elsys.healthmap.databinding.FragmentGymEditBinding
import org.elsys.healthmap.ui.viewmodels.GymEditViewModel

class GymEditFragment : Fragment() {
    private val gymEditViewModel: GymEditViewModel by viewModels()
    private var isChanged = false
    private val args: GymEditFragmentArgs by navArgs()
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//            val myUri = Uri.parse("content://media/picker/0/com.android.providers.media.photopicker/media/1980001135")
            if (uri != null) {
                lifecycleScope.launch {
                    gymEditViewModel.addPhoto(
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
        gymEditViewModel.gymId = args.id

        val adapter = GymImagesAdapter(
            gymEditViewModel.photos,
            requireContext().cacheDir,
            gymEditViewModel.viewModelScope
        ) {
            gymEditViewModel.viewModelScope.launch {
                gymEditViewModel.deletePhoto(it)
            }
        }

        gymEditViewModel.photos.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        val gymImagesRecyclerView = binding.gymImagesRecyclerView
        gymImagesRecyclerView.adapter = adapter
        gymImagesRecyclerView.setHasFixedSize(false)

        gymEditViewModel.gym.observe(viewLifecycleOwner) {
            binding.gym = gymEditViewModel.gym.value
            binding.tags = gymEditViewModel.gym.value?.tags?.joinToString(", ")
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
                gymEditViewModel.gym.value?.address = address
            }
            gymEditViewModel.gym.value?.geohash = hash
            gymEditViewModel.gym.value?.coordinates = GeoPoint(lat, lng)
            isChanged = true
        }

        val priceTable = binding.priceTableRecyclerView
        val priceTableDataset = gymEditViewModel.priceTable
        priceTable.adapter = GymEditPriceTableAdapter(priceTableDataset) { product ->
            gymEditViewModel.deletePriceTableElement(product)
        }

        gymEditViewModel.priceTable.observe(viewLifecycleOwner) {
            priceTable.adapter?.notifyDataSetChanged()
        }

        priceTable.setHasFixedSize(false)

        binding.addPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.gymName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                gymEditViewModel.gym.value?.name = s.toString()
                isChanged = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.tagsField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                gymEditViewModel.gym.value?.tags = s.toString().split(", ") as MutableList<String>
                isChanged = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                gymEditViewModel.gym.value?.description = s.toString()
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
            gymEditViewModel.addPriceTableElement(bundle)
        }

        gymEditViewModel.isUploadImageSuccessful.observe(viewLifecycleOwner) {
            if(it == false) {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()

        gymEditViewModel.viewModelScope.launch {
            if (isChanged) {
                gymEditViewModel.saveGym()
                isChanged = false
            }
        }
    }

}