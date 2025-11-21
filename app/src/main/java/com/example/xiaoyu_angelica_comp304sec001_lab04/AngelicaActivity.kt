package com.example.xiaoyu_angelica_comp304sec001_lab04

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.xiaoyu_angelica_comp304sec001_lab04.ui.theme.OsakaTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class AngelicaActivity : ComponentActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var googleMap: GoogleMap? = null

    private var placeName: String? = null
    private var lat: Double = 34.6873
    private var lng: Double = 135.5259

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startLocationUpdates()
            else Toast.makeText(this, "Permission required", Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        placeName = intent.getStringExtra("placeName")
        lat = intent.getDoubleExtra("lat", lat)
        lng = intent.getDoubleExtra("lng", lng)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (loc in result.locations) updateLocationOnMap(loc)
            }
        }

        setContent {
            OsakaTheme {
                MapScreenWithBack { finish() }
            }
        }
    }

    // Remember MapView and tie it to the Compose lifecycle
    @Composable
    private fun rememberMapViewWithLifecycle(context: Context): MapView {
        val mapView = remember { MapView(context) }
        val lifecycle = LocalLifecycleOwner.current.lifecycle

        DisposableEffect(lifecycle, mapView) {
            val observer = object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) { mapView.onCreate(null) }
                override fun onStart(owner: LifecycleOwner) { mapView.onStart() }
                override fun onResume(owner: LifecycleOwner) { mapView.onResume() }
                override fun onPause(owner: LifecycleOwner) { mapView.onPause() }
                override fun onStop(owner: LifecycleOwner) { mapView.onStop() }
                override fun onDestroy(owner: LifecycleOwner) { mapView.onDestroy() }
            }
            lifecycle.addObserver(observer)
            onDispose { lifecycle.removeObserver(observer) }
        }

        return mapView
    }

    @Composable
    fun MapScreenWithBack(onBack: () -> Unit) {
        val context = LocalContext.current
        val mapView = rememberMapViewWithLifecycle(context)

        Box(modifier = Modifier.fillMaxSize()) {
            // Map host (unchanged map logic)
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize(),
                update = { mv -> mv.getMapAsync(this@AngelicaActivity) }
            )

            // Top-left back button overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .widthIn(min = 88.dp)
                ) {
                    Text(text = "Back", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true

        checkPermissionAndStart()

        val target = LatLng(lat, lng)
        map.addMarker(MarkerOptions().position(target).title(placeName ?: "Selected Place"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15f))
    }

    private fun checkPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED -> startLocationUpdates()

            else -> permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
}