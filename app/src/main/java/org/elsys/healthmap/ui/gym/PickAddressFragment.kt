package org.elsys.healthmap.ui.gym

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import org.elsys.healthmap.R
import org.elsys.healthmap.databinding.FragmentPickAddressBinding

class PickAddressFragment : Fragment() {
    private lateinit var locationManager: LocationManager
    private lateinit var map: SupportMapFragment
    private val locationPermissionCode = 2

    // FIXME check related comments in UserActivity, also - code duplication
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(
            requireContext(),
            LocationManager::class.java
        )!!
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            5f,
            object : android.location.LocationListener {
                override fun onLocationChanged(location: Location) {
                    map.getMapAsync {
                        it.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                16f
                            )
                        )
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            })
    }


    // FIXME This code will not work on versions of Android below 33 due to the call to Geocoder.getFromLocation
    //  you should either have a check for the current version (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
    //  and provide the functionality only on newer
    //  devices or use the deprecated blocking call with a coroutine launched in lifecycleScope
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPickAddressBinding.inflate(inflater, container, false)

        map = childFragmentManager.findFragmentById(R.id.mapPicker) as SupportMapFragment
        getLocation()

        // TODO: Version based query

        binding.pickAddressButton.setOnClickListener {
            map.getMapAsync {
                val center = it.cameraPosition.target

                Geocoder(requireContext()).getFromLocation(
                    center.latitude,
                    center.longitude,
                    1
                ) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0].getAddressLine(0).split(",")[0]

                        Log.d("ADDRESS", address)

                        // FIXME have constants for all these keys
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

        return binding.root
    }
}