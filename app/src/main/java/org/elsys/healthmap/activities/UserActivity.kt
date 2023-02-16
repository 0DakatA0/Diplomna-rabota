package org.elsys.healthmap.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import org.elsys.healthmap.R
import org.elsys.healthmap.databinding.ActivityUserBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.repositories.GymsRepository
import org.elsys.healthmap.ui.user.BottomSheetGymFragment
import org.elsys.healthmap.ui.viewmodels.UserViewModel
import kotlin.math.cos

class UserActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var map: SupportMapFragment
    private val locationPermissionCode = 2
    private val viewModel: UserViewModel by viewModels()

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = ContextCompat.getSystemService(
            this,
            LocationManager::class.java
        )!!
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
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
                            newLatLngZoom(
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

    private fun calculateRadius(googleMap: GoogleMap): Double {
        val earthRadius = 6378137.0
        val mapWidth = map.view?.width
        val zoomLevel = googleMap.cameraPosition.zoom
        val groundResolution =
            earthRadius * cos(Math.toRadians(googleMap.cameraPosition.target.latitude)) / (Math.pow(
                2.0,
                zoomLevel.toDouble()
            ))
        return groundResolution * mapWidth!! / 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUserBinding.inflate(layoutInflater)

        map = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.map, map)
            .commit()

        getLocation()

        map.getMapAsync {
            it.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))

            it.setOnCameraIdleListener {
                val radius = calculateRadius(it)
                val center =
                    GeoLocation(
                        it.cameraPosition.target.latitude,
                        it.cameraPosition.target.longitude
                    )

                lifecycleScope.launch {
                    it.clear()

                    var gyms = GymsRepository.getGymsByLocation(center, radius)

                    Log.d("UserActivity", gyms.toString())

                    val searchBar = binding.searchBar
                    val search = searchBar.query.toString()

                    if(search.isNotEmpty()) {
                        if (search[0] == '#') {
                            gyms.filter { gym ->
                                val tags = gym.tags
                                tags.forEach { tag ->
                                    if (tag.contains(search.substring(1))) return@filter true
                                }

                                return@filter false
                            }
                        } else {
                            gyms = gyms.filter { gym ->
                                return@filter gym.name.contains(search)
                            }
                        }
                    }

                    gyms.forEach { gym ->
                        val markerOptions = MarkerOptions()
                            .position(
                                LatLng(
                                    gym.coordinates.latitude,
                                    gym.coordinates.longitude
                                )
                            )
                            .title(gym.name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                        it.addMarker(markerOptions)?.tag = gym
                    }

                    it.setOnMarkerClickListener { marker ->
                        val modalBottomSheet = BottomSheetGymFragment(marker.tag as Gym, viewModel)
                        modalBottomSheet.show(supportFragmentManager, BottomSheetGymFragment.TAG)
                        return@setOnMarkerClickListener true
                    }
                }
            }
        }

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLogOut -> {
                startActivity(Intent(this, AuthenticationActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}