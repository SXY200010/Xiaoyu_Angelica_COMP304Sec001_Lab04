package com.example.xiaoyu_angelica_comp304sec001_lab04

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class AngelicaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val placeName = intent.getStringExtra("placeName") ?: ""
        val placeUrl = intent.getStringExtra("placeUrl") ?: ""
        val placeLat = intent.getDoubleExtra("lat", 0.0)
        val placeLng = intent.getDoubleExtra("lng", 0.0)

        val target = LatLng(placeLat, placeLng)

        setContent {
            PlaceDetailMapScreen(
                placeName = placeName,
                target = target,
                url = placeUrl,
                onBack = { finish() }
            )
        }
    }
}

@Composable
fun PlaceDetailMapScreen(
    placeName: String,
    target: LatLng,
    url: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var mapType by remember { mutableStateOf(MapType.NORMAL) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(target, 15f)
    }

    Box(Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = mapType),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false
            )
        ) {
            Marker(
                state = MarkerState(position = target),
                title = placeName
            )
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D3EAF),
                contentColor = Color.White
            )
        ) {
            Text("Back")
        }

        if (url.isNotBlank()) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    try { context.startActivity(intent) } catch (_: Exception) {}
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    contentColor = Color.White
                )
            ) {
                Text("Website")
            }
        }

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .fillMaxWidth()
                .background(Color(0xAA000000))
                .padding(10.dp)
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
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(type.name)
                    }
                }
            }
        }
    }
}
