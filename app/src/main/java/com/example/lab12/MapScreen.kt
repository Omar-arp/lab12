package com.example.lab12

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import androidx.compose.ui.Alignment


fun loadBitmapDescriptor(context: Context): BitmapDescriptor {
    val original = BitmapFactory.decodeResource(context.resources, R.drawable.bombero)
    val scaled = Bitmap.createScaledBitmap(original, 80, 80, false)
    return BitmapDescriptorFactory.fromBitmap(scaled)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("MissingPermission")
fun MapScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locations = listOf(
        LatLng(-16.433415, -71.5442652), // JLByR
        LatLng(-16.4205151, -71.4945209), // Paucarpata
        LatLng(-16.3524187, -71.5675994)  // Zamacola
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(locations[0], 12f)
    }

    val iconState = remember { mutableStateOf<BitmapDescriptor?>(null) }
    val showLocation = remember { mutableStateOf(false) }
    val userLocation = remember { mutableStateOf<LatLng?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        userLocation.value = LatLng(it.latitude, it.longitude)
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLocation.value!!, 15f))
                    }
                }
            }
        }
    )

    val polygon1 = listOf(
        LatLng(-16.432292, -71.509145),
        LatLng(-16.432757, -71.509626),
        LatLng(-16.433013, -71.509310),
        LatLng(-16.432566, -71.508853)
    )

    val mapTypeState = remember { mutableStateOf(MapType.NORMAL) }
    val expanded = remember { mutableStateOf(false) }
    val mapLabels = listOf("Normal", "Satélite", "Terreno", "Híbrido")
    val mapTypes = listOf(MapType.NORMAL, MapType.SATELLITE, MapType.TERRAIN, MapType.HYBRID)

    // Cargar ícono personalizado
    LaunchedEffect(Unit) {
        iconState.value = loadBitmapDescriptor(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = mapTypeState.value)
        ) {
            // Marcadores fijos
            iconState.value?.let { icon ->
                locations.forEach {
                    Marker(
                        state = rememberMarkerState(position = it),
                        icon = icon,
                        title = "Ubicación",
                        snippet = "Punto de interés"
                    )
                }
            }

            // Polígono
            Polygon(points = polygon1, strokeColor = Color.Red, fillColor = Color.Blue, strokeWidth = 5f)

            // Marcador de ubicación actual si existe
            userLocation.value?.let {
                Marker(
                    state = rememberMarkerState(position = it),
                    title = "Estás aquí",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
        }

        // Menú desplegable (mapas en español)
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            TextField(
                readOnly = true,
                value = mapLabels[mapTypes.indexOf(mapTypeState.value)],
                onValueChange = {},
                label = { Text("Tipo de Mapa") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                mapLabels.zip(mapTypes).forEach { (label, type) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            mapTypeState.value = type
                            expanded.value = false
                        }
                    )
                }
            }
        }

        // Botón para mostrar ubicación
        Button(
            onClick = {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text("Mostrar mi ubicación")
        }
    }
}
