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
import androidx.compose.material3.* // Usamos Material3 para el menú
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding


// Función para cargar y escalar un ícono personalizado (bombero)
fun loadBitmapDescriptor(context: Context): BitmapDescriptor {
    val original = BitmapFactory.decodeResource(context.resources, R.drawable.bombero)
    val scaled = Bitmap.createScaledBitmap(original, 80, 80, false)
    return BitmapDescriptorFactory.fromBitmap(scaled)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current

    // Ubicaciones de los marcadores
    val locations = listOf(
        LatLng(-16.433415, -71.5442652), // JLByR
        LatLng(-16.4205151, -71.4945209), // Paucarpata
        LatLng(-16.3524187, -71.5675994)  // Zamacola
    )

    // Control de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(locations[0], 12f) // inicia en JLByR
    }

    // Estado para el ícono personalizado
    val iconState = remember { mutableStateOf<BitmapDescriptor?>(null) }

    // Coordenadas de los polígonos
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

    // Polilínea que conecta los 3 puntos
    val rutaEjemplo = listOf(
        LatLng(-16.433415, -71.5442652),  // JLByR
        LatLng(-16.4205151, -71.4945209), // Paucarpata
        LatLng(-16.3524187, -71.5675994)  // Zamacola
    )

    // Estado para el tipo de mapa
    val mapTypeState = remember { mutableStateOf(MapType.NORMAL) }
    val expanded = remember { mutableStateOf(false) }
    val mapTypes = listOf("Normal", "Satellite", "Terrain", "Hybrid")

    // Carga del ícono al iniciar
    LaunchedEffect(Unit) {
        iconState.value = loadBitmapDescriptor(context)
    }

    // Movimiento animado de la cámara hacia Yura
    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984, -71.6836503), 12f),
            durationMs = 3000
        )
    }

    // UI principal
    Box(modifier = Modifier.fillMaxSize()) {

        // Mapa de Google con todo lo interactivo
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = mapTypeState.value) //  AQUÍ usamos mapType correctamente
        ) {
            // Marcadores personalizados
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

            // Polígonos (áreas)
            Polygon(points = plazaDeArmasPolygon, strokeColor = Color.Red, fillColor = Color.Blue, strokeWidth = 5f)
            Polygon(points = parqueLambramaniPolygon, strokeColor = Color.Red, fillColor = Color.Blue, strokeWidth = 5f)
            Polygon(points = mallAventuraPolygon, strokeColor = Color.Red, fillColor = Color.Blue, strokeWidth = 5f)

            // Polilínea (ruta)
            Polyline(points = rutaEjemplo, color = Color.Black, width = 5f)
        }

        // Menú desplegable para cambiar tipo de mapa
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value },
            modifier = Modifier.align(androidx.compose.ui.Alignment.TopStart)
        ) {
            TextField(
                readOnly = true,
                value = when (mapTypeState.value) {
                    MapType.NORMAL -> "Normal"
                    MapType.SATELLITE -> "Satellite"
                    MapType.TERRAIN -> "Terrain"
                    MapType.HYBRID -> "Hybrid"
                    else -> "Normal"
                },
                onValueChange = {},
                label = { Text("Tipo de Mapa") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
                modifier = Modifier.menuAnchor().padding(8.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                mapTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            mapTypeState.value = when (type) {
                                "Satelite" -> MapType.SATELLITE
                                "Terreno" -> MapType.TERRAIN
                                "Hibrido" -> MapType.HYBRID
                                else -> MapType.NORMAL
                            }
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}
