package com.example.xiaoyu_angelica_comp304sec001_lab04

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.Place
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.PlacesRepository
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class XiaoyuActivity : ComponentActivity(), OnMapReadyCallback {

    // --- Original map-related fields (kept exactly with same behavior) ---
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var googleMap: GoogleMap? = null

    // Permission launcher for map/location
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startLocationUpdates()
            else Toast.makeText(this, "Permission required", Toast.LENGTH_LONG).show()
        }

    // --- Activity state to choose which UI to show ---
    private var showPlacesList = false
    private var categoryIdPassed: String? = null

    // Map default place (kept as Osaka default)
    private var placeNameFromIntent: String? = null
    private var latFromIntent: Double = 34.6873
    private var lngFromIntent: Double = 135.5259

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Decide mode:
        // If launched with "categoryId" we show the places list,
        // otherwise we show the original map view. This preserves original map code.
        categoryIdPassed = intent.getStringExtra("categoryId")
        showPlacesList = !categoryIdPassed.isNullOrEmpty()

        // Still read map extras in case the activity is launched to show the place on the map
        placeNameFromIntent = intent.getStringExtra("placeName")
        latFromIntent = intent.getDoubleExtra("lat", latFromIntent)
        lngFromIntent = intent.getDoubleExtra("lng", lngFromIntent)

        // Initialize location clients and requests (same as original)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (loc in result.locations) updateLocationOnMap(loc)
            }
        }

        setContent {
            MaterialTheme {
                if (showPlacesList) {
                    // Show places list UI (second screen)
                    PlacesListScreen(
                        places = PlacesRepository.getPlaces(categoryIdPassed ?: ""),
                        onPlaceClick = { place ->
                            val intent = Intent(this, AngelicaActivity::class.java).apply {
                                putExtra("placeId", place.id)
                                putExtra("placeName", place.name)
                                putExtra("lat", place.latitude)
                                putExtra("lng", place.longitude)
                            }
                            startActivity(intent)
                        }
                    )
                } else {
                    // Show original Map screen (kept intact)
                    MapScreen()
                }
            }
        }
    }

    // -------------------------
    // Places list UI (new)
    // -------------------------
    @Composable
    fun PlacesListScreen(places: List<Place>, onPlaceClick: (Place) -> Unit) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Select a place", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(places) { place ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onPlaceClick(place) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = place.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = place.address, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }

    // -------------------------
    // Original Map UI (kept)
    // -------------------------
    @Composable
    fun MapScreen() {
        val context = LocalContext.current
        val lifecycle = LocalLifecycleOwner.current.lifecycle

        val mapView = remember {
            MapView(context).apply { onCreate(null) }
        }

        DisposableEffect(lifecycle, mapView) {
            val observer = object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) = mapView.onStart()
                override fun onResume(owner: LifecycleOwner) = mapView.onResume()
                override fun onPause(owner: LifecycleOwner) = mapView.onPause()
                override fun onStop(owner: LifecycleOwner) = mapView.onStop()
                override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
            }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }

        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize()) {
            it.getMapAsync(this@XiaoyuActivity)
        }
    }

    // Original OnMapReady (unchanged logic except uses intent-provided lat/lng/name if available)
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true

        checkPermissionAndStart()

        // If the activity was launched without categoryId, show the place from intent (if any).
        val target = LatLng(latFromIntent, lngFromIntent)
        map.addMarker(MarkerOptions().position(target).title(placeNameFromIntent ?: "Selected Place"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 14f))
    }

    // Permission + start location updates (kept)
    private fun checkPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> startLocationUpdates()

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT).show()
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        googleMap?.isMyLocationEnabled = true
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun updateLocationOnMap(location: Location) {
        val map = googleMap ?: return
        val latLng = LatLng(location.latitude, location.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (_: Exception) {
            // ignore
        }
    }
}