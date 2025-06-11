package com.example.lab12

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


    val mallAventuraPolygon = listOf(
        LatLng(-16.432292, -71.509145),
        LatLng(-16.432757, -71.509626),
        LatLng(-16.433013, -71.509310),
        LatLng(-16.432566, -71.508853)
    )

    val parqueLambramaniPolygon = listOf(
        LatLng(-16.422704, -71.530830),
        LatLng(-16.422920, -71.531340),
        LatLng(-16.423264, -71.531110),
        LatLng(-16.423050, -71.530600)
    )

    val plazaDeArmasPolygon = listOf(
        LatLng(-16.398866, -71.536961),
        LatLng(-16.398744, -71.536529),
        LatLng(-16.399178, -71.536289),
        LatLng(-16.399299, -71.536721)
    )

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
            // ✅ Tus marcadores
            iconState.value?.let { icon ->
                locations.forEach { location ->
                    Marker(
                        state = rememberMarkerState(position = location),
                        icon = icon,
                        title = "Ubicación",
                        snippet = "Punto de interés"
                    )
                }
            }

            // ✅ TUS POLÍGONOS AQUÍ DENTRO
            Polygon(
                points = plazaDeArmasPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue,
                strokeWidth = 5f
            )
            Polygon(
                points = parqueLambramaniPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue,
                strokeWidth = 5f
            )
            Polygon(
                points = mallAventuraPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue,
                strokeWidth = 5f
            )
        }
    }


}
