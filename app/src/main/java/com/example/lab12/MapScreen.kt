@Composable
fun MapScreen() {
    val context = LocalContext.current

    val locations = listOf(
        LatLng(-16.433415,-71.5442652), // JLByR
        LatLng(-16.4205151,-71.4945209), // Paucarpata
        LatLng(-16.3524187,-71.5675994)  // Zamacola
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(locations[0], 12f) // Inicio en JLByR
    }

    val iconState = remember { mutableStateOf<BitmapDescriptor?>(null) }

    // Cargar ícono personalizado
    LaunchedEffect(Unit) {
        iconState.value = loadBitmapDescriptor(context)
    }

    // 🎯 Mover cámara a Yura después de 0 ms con animación
    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984, -71.6836503), 12f),
            durationMs = 3000 // 3 segundos
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
                        title = "Punto de interés"
                    )
                }
            }
        }
    }
}
