package com.example.lab12

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

fun loadBitmapDescriptor(context: Context): BitmapDescriptor {
    val original = BitmapFactory.decodeResource(context.resources, R.drawable.bombero)
    val scaled = Bitmap.createScaledBitmap(original, 80, 80, false) // escala manual
    return BitmapDescriptorFactory.fromBitmap(scaled)
}

@Composable
fun MapScreen() {
    val context = LocalContext.current
    //val arequipaLocation = LatLng(-16.4040102, -71.559611)
    val locations = listOf(
        LatLng(-16.433415,-71.5442652), // JLByR
        LatLng(-16.4205151,-71.4945209), // Paucarpata
        LatLng(-16.3524187,-71.5675994) // Zamacola
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(locations[0], 12f)
    }

    val iconState = remember { mutableStateOf<BitmapDescriptor?>(null) }

    // Cargar ícono UNA VEZ de forma segura
    LaunchedEffect(Unit) {
        iconState.value = loadBitmapDescriptor(context)
    }

    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984,-71.6836503), 12f), // Mover a Yura
            durationMs = 3000
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            iconState.value?.let { icon ->
                locations.forEach { location ->
                    Marker(
                        state = rememberMarkerState(position = location),
                        icon = icon,
                        title = "Ubicacion",
                        snippet = "Punto de interés"
                    )
                }
            }
        }
    }
}
