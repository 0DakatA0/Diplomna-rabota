package org.elsys.healthmap.ui.gym

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import org.elsys.healthmap.databinding.FragmentPickAddressBinding

class PickAddressFragment : Fragment() {
    private lateinit var mapbox: MapView
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    private fun getLocation() {
        locationManager = getSystemService(requireContext(), LocationManager::class.java)!!
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
                    mapbox.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(fromLngLat(location.longitude, location.latitude)).zoom(16.0)
                            .build())
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPickAddressBinding.inflate(inflater, container, false)

        mapbox = binding.mapView
        getLocation()

        mapbox.getMapboxMap().addOnCameraChangeListener {
            val center = mapbox.getMapboxMap().cameraState.center
            Toast.makeText(requireContext(), center.latitude().toString(), Toast.LENGTH_SHORT)
                .show()
        }

        binding.pickAddressButton.setOnClickListener {
            val center = mapbox.getMapboxMap().cameraState.center
            setFragmentResult(
                "PICK_ADDRESS",
                bundleOf("latitude" to center.latitude(), "longitude" to center.longitude())
            )
            findNavController().popBackStack()
        }

        return binding.root
    }
}