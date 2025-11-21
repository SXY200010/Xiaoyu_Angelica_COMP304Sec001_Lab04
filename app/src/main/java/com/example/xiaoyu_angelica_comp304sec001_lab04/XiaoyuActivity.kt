package com.example.xiaoyu_angelica_comp304sec001_lab04

import android.Manifest
import android.content.Intent
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.Place
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.PlacesRepository
import com.example.xiaoyu_angelica_comp304sec001_lab04.ui.theme.OsakaTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class XiaoyuActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null

    private var showPlacesList = false
    private var categoryIdPassed: String? = null

    private var placeNameFromIntent: String? = null
    private var latFromIntent: Double = 34.6873
    private var lngFromIntent: Double = 135.5259

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startLocationUpdates()
            else Toast.makeText(this, "Permission required", Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categoryIdPassed = intent.getStringExtra("categoryId")
        showPlacesList = !categoryIdPassed.isNullOrEmpty()

        placeNameFromIntent = intent.getStringExtra("placeName")
        latFromIntent = intent.getDoubleExtra("lat", latFromIntent)
        lngFromIntent = intent.getDoubleExtra("lng", lngFromIntent)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { lastLocation = it }
            }
        }

        setContent {
            OsakaTheme {
                if (showPlacesList) {
                    PlacesListScreen(
                        places = PlacesRepository.getPlaces(this, categoryIdPassed ?: ""),
                        onPlaceClick = { place ->
                            val intent = Intent(this, AngelicaActivity::class.java).apply {
                                putExtra("placeId", place.id)
                                putExtra("placeName", place.name)
                                putExtra("lat", place.latitude)
                                putExtra("lng", place.longitude)
                            }
                            startActivity(intent)
                        },
                        onBack = { finish() }
                    )
                } else {
                    MapScreen()
                }
            }
        }
    }

    @Composable
    fun PlacesListScreen(places: List<Place>, onPlaceClick: (Place) -> Unit, onBack: () -> Unit) {
        val ctx = LocalContext.current
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background_main),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(Color(0x88FFFFFF)))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.size(110.dp, 44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Places",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                                val imageRes = ctx.resources.getIdentifier(
                                    place.id,
                                    "drawable",
                                    ctx.packageName
                                ).let { if (it == 0) R.drawable.placeholder_place else it }

                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = place.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(place.name, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(6.dp))
                                Text(place.address, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MapScreen() {
        val target = LatLng(latFromIntent, lngFromIntent)
        var mapType by remember { mutableStateOf(MapType.NORMAL) }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(target, 14f)
        }
        val hasPermission = hasLocationPermission()
        var mapProperties by remember(mapType, lastLocation, hasPermission) {
            mutableStateOf(
                MapProperties(
                    mapType = mapType,
                    isMyLocationEnabled = hasPermission
                )
            )
        }
        var uiSettings by remember {
            mutableStateOf(
                MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true
                )
            )
        }

        LaunchedEffect(true) { checkPermissionAndStart() }

        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = mapProperties,
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = target),
                    title = placeNameFromIntent ?: "Selected Place"
                )
            }

            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(10f)
                    .fillMaxWidth()
                    .background(Color(0xAAFFFFFF))
                    .padding(1.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        MapType.NORMAL,
                        MapType.SATELLITE,
                        MapType.TERRAIN,
                        MapType.HYBRID
                    ).forEach { type ->
                        Button(
                            onClick = { mapType = type },
                            modifier = Modifier.weight(0.5f).padding(horizontal = 1.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = type.name,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionAndStart() {
        when {
            hasLocationPermission() -> startLocationUpdates()
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT).show()
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startLocationUpdates() {
        if (!hasLocationPermission()) return
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
