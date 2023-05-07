package org.elsys.healthmap.activities

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import org.elsys.healthmap.R
import org.elsys.healthmap.databinding.ActivityUserBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.ui.user.BottomSheetGymFragment
import org.elsys.healthmap.ui.viewmodels.UserViewModel
import kotlin.math.cos

class UserActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: SupportMapFragment
    private val viewModel: UserViewModel by viewModels()

    @SuppressLint("MissingPermission")
    private fun getLocation(map: GoogleMap) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.getCurrentLocation(
            100, null
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                map.moveCamera(
                    newLatLngZoom(
                        LatLng(location.latitude, location.longitude), 16f
                    )
                )
            }
        }
    }

    private fun handleLocationPermissions(map: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation(map)
        } else {
            val builder = AlertDialog.Builder(this)

            builder.apply {
                setPositiveButton("Grant Permission") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@UserActivity,
                        arrayOf(
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION
                        ),
                        2
                    )

                    if (ContextCompat.checkSelfPermission(
                            this@UserActivity, ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                            this@UserActivity, ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        getLocation(map)
                    }
                }
                setNegativeButton("No, Thanks") { _, _ -> }
            }
            builder.setMessage("Location permission is required to assist you in finding gyms near you. Would you like to grant it?")
            builder.create().show()
        }
    }

    private fun calculateRadius(googleMap: GoogleMap): Double {
        val earthRadius = 6378137.0
        val mapWidth = map.view?.width
        val zoomLevel = googleMap.cameraPosition.zoom
        val groundResolution =
            earthRadius * cos(Math.toRadians(googleMap.cameraPosition.target.latitude)) / (Math.pow(
                2.0, zoomLevel.toDouble()
            ))
        return groundResolution * mapWidth!! / 2
    }

    private fun updateMarkers(gyms: List<Gym>, map: GoogleMap) {
        map.clear()

        gyms.forEach { gym ->
            val markerOptions = MarkerOptions().position(
                LatLng(
                    gym.coordinates.latitude, gym.coordinates.longitude
                )
            ).title(gym.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

            map.addMarker(markerOptions)?.tag = gym
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityUserBinding.inflate(layoutInflater)

        map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)

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

    override fun onMapReady(map: GoogleMap) {
        handleLocationPermissions(map)
        map.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))

        viewModel.gyms.observe(this@UserActivity) { gyms ->
            updateMarkers(gyms, map)
        }

        val searchBar = findViewById<SearchView>(R.id.searchBar)

        map.setPadding(0, searchBar.height + 30, 0, 0)

        map.uiSettings.isCompassEnabled = true

        map.setOnMarkerClickListener { marker ->
            val modalBottomSheet = BottomSheetGymFragment(marker.tag as Gym)
            modalBottomSheet.show(supportFragmentManager, BottomSheetGymFragment.TAG)
            return@setOnMarkerClickListener true
        }

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val radius = calculateRadius(map)
                val center = GeoLocation(
                    map.cameraPosition.target.latitude, map.cameraPosition.target.longitude
                )

                viewModel.updateGyms(center, radius, newText!!)

                return true
            }
        })

        map.setOnCameraIdleListener {
            val search = searchBar.query.toString()

            val radius = calculateRadius(map)
            val center = GeoLocation(
                map.cameraPosition.target.latitude, map.cameraPosition.target.longitude
            )

            viewModel.updateGyms(center, radius, search)
        }
    }
}
