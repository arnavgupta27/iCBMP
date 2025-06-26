package com.example.icbmpfinalboss.ui.screens.map

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.icbmpfinalboss.R
import com.example.icbmpfinalboss.data.MockMapData
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen() {
    val context = LocalContext.current
    var showVehicleList by remember { mutableStateOf(false) }
    var showStationList by remember { mutableStateOf(false) }

    // Mutable state for camera position - this allows us to control zoom and center
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(73.8567, 18.5204))
            zoom(10.5)
            pitch(0.0)
            bearing(0.0)
        }
    }

    val carIcon = remember {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_car_marker)
        IconImage(bitmap)
    }
    val chargerIcon = remember {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_charger_marker)
        IconImage(bitmap)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState
        ) {
            MockMapData.mockVehicles.forEach { vehicle ->
                PointAnnotation(
                    point = Point.fromLngLat(vehicle.longitude, vehicle.latitude)
                ) {
                    iconImage = carIcon
                    iconSize = 0.1
                }
            }

            MockMapData.mockChargingStations.forEach { station ->
                PointAnnotation(
                    point = Point.fromLngLat(station.longitude, station.latitude)
                ) {
                    iconImage = chargerIcon
                    iconSize = 0.08
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { showVehicleList = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("🚗")
            }

            FloatingActionButton(
                onClick = { showStationList = true },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Text("⚡")
            }
        }
    }

    if (showVehicleList) {
        ModalBottomSheet(
            onDismissRequest = { showVehicleList = false }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🚗 Fleet Vehicles",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn {
                    items(MockMapData.mockVehicles) { vehicle ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                // 🎯 THE MAGIC HAPPENS HERE - Zoom in on vehicle location
                                mapViewportState.setCameraOptions(
                                    CameraOptions.Builder()
                                        .center(Point.fromLngLat(vehicle.longitude, vehicle.latitude))
                                        .zoom(15.0) // Close-up zoom level
                                        .build()
                                )
                                showVehicleList = false // Close the bottom sheet
                            }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "${vehicle.id} - ${vehicle.model}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("🔋 Battery: ${vehicle.currentSoc}%")
                                Text("📍 Status: ${vehicle.status}")
                                Text("📍 Tap to locate on map")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showStationList) {
        ModalBottomSheet(
            onDismissRequest = { showStationList = false }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⚡ Charging Stations",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn {
                    items(MockMapData.mockChargingStations) { station ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                // 🎯 THE MAGIC HAPPENS HERE - Zoom in on charging station location
                                mapViewportState.setCameraOptions(
                                    CameraOptions.Builder()
                                        .center(Point.fromLngLat(station.longitude, station.latitude))
                                        .zoom(15.0) // Close-up zoom level
                                        .build()
                                )
                                showStationList = false // Close the bottom sheet
                            }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = station.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("🔌 Available: ${station.availableConnectors}/${station.totalConnectors}")
                                Text("⚡ Type: ${station.type}")
                                Text("📍 Tap to locate on map")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
