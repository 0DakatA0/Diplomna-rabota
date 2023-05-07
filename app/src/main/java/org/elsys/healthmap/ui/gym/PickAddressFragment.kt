package org.elsys.healthmap.ui.gym

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.elsys.healthmap.R
import org.elsys.healthmap.databinding.FragmentPickAddressBinding
import java.util.*

class PickAddressFragment : Fragment() {
    private lateinit var map: SupportMapFragment

    @SuppressLint("MissingPermission")
    private fun getLocation(map: GoogleMap) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.getCurrentLocation(
            100, null
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude), 16f
                    )
                )
            }
        }
    }

    private fun handleLocationPermissions(map: GoogleMap) {
        //TODO change location query method
        if (ContextCompat.checkSelfPermission(
                requireContext(), ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation(map)
        } else {
            val builder = AlertDialog.Builder(requireContext())

            builder.apply {
                setPositiveButton("Grant Permission") { _, _ ->
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION
                        ),
                        2
                    )

                    getLocation(map)
                }
                setNegativeButton("No, Thanks") { _, _ -> }
            }
            builder.setMessage("Location permission is required to assist you in finding gyms near you. Would you like to grant it?")
            builder.create().show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPickAddressBinding.inflate(inflater, container, false)

        map = childFragmentManager.findFragmentById(R.id.mapPicker) as SupportMapFragment

        binding.pickAddressButton.setOnClickListener {
            map.getMapAsync {
                val center = it.cameraPosition.target

                handleLocationPermissions(it)

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Geocoder(requireContext()).getFromLocation(
                        center.latitude,
                        center.longitude,
                        1
                    ) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0].getAddressLine(0).split(",")[0]

                            setFragmentResult(
                                "PICK_ADDRESS",
                                bundleOf(
                                    "latitude" to center.latitude,
                                    "longitude" to center.longitude,
                                    "address" to address
                                )
                            )
                            findNavController().popBackStack()
                        }
                    }
                } else {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())

                    lifecycleScope.launch(Dispatchers.IO) {
                        val addresses = geocoder.getFromLocation(
                            center.latitude,
                            center.longitude,
                            1
                        )

                        if (addresses!!.isNotEmpty()) {
                            val address = addresses[0].getAddressLine(0).split(",")[0]

                            setFragmentResult(
                                "PICK_ADDRESS",
                                bundleOf(
                                    "latitude" to center.latitude,
                                    "longitude" to center.longitude,
                                    "address" to address
                                )
                            )
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }

        return binding.root
    }
}