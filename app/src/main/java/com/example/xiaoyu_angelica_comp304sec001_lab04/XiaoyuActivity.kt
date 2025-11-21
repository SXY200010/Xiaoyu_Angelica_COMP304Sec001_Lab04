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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.Place
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.PlacesRepository
import com.example.xiaoyu_angelica_comp304sec001_lab04.ui.theme.OsakaTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.compose.ui.viewinterop.AndroidView

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
            // Use the OsakaTheme even if some theme files are not used now
            OsakaTheme {
                if (showPlacesList) {
                    // Show enhanced places list UI (second screen)
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
                        },
                        onBack = { finish() } // return to main
                    )
                } else {
                    // Show original Map screen (kept intact)
                    MapScreen()
                }
            }
        }
    }

    // -------------------------
    // Enhanced Places list UI
    // -------------------------
    @Composable
    fun PlacesListScreen(places: List<Place>, onPlaceClick: (Place) -> Unit, onBack: () -> Unit) {
        val ctx = LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {
            // Background image (same one as Main or different if you prefer)
            Image(
                painter = painterResource(id = R.drawable.background_main), // or R.drawable.background_places
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // subtle overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = androidx.compose.ui.graphics.Color(0x88FFFFFF))
            )

            // Existing content placed on top
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {

                // Top bar: Back + Title centered
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.size(width = 110.dp, height = 44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Places",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(places) { place ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPlaceClick(place) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(14.dp)
                            ) {

                                // Resolve image resource by place id, fallback to placeholder
                                val imageRes = remember(place.id) {
                                    val resId = ctx.resources.getIdentifier(
                                        place.id,
                                        "drawable",
                                        ctx.packageName
                                    )
                                    if (resId == 0) R.drawable.placeholder_place else resId
                                }

                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = place.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(place.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(place.address, style = MaterialTheme.typography.bodyMedium)
                            }
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

        val mapView = remember { MapView(context).apply { onCreate(null) } }

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

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { mv -> mv.getMapAsync(this@XiaoyuActivity) }
        )
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