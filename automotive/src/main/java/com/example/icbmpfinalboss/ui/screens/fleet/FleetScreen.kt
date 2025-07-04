package com.example.icbmpfinalboss.ui.screens.fleet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.icbmpfinalboss.data.models.BmsData
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icbmpfinalboss.viewmodel.FleetListUiState
import com.example.icbmpfinalboss.viewmodel.FleetViewModel

@Composable
fun FleetScreen(
    fleetViewModel: FleetViewModel = viewModel()
) {
    val uiState by fleetViewModel.fleetListState.collectAsState()
    var selectedVehicle by remember { mutableStateOf<BmsData?>(null) }

    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        when (val state = uiState) {
            is FleetListUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is FleetListUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is FleetListUiState.Success -> {
                if (state.fleet.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No other vehicles in fleet.")
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.fleet, key = { vehicle -> vehicle.bmsId }) { vehicle ->
                            FleetVehicleItem(vehicleData = vehicle) {
                                // THE FIX #1: Was 'selectedVehicle = it', now correctly uses 'vehicle'
                                selectedVehicle = vehicle
                            }
                        }
                    }
                }
            }
        }

        selectedVehicle?.let { vehicle ->
            FleetVehicleDetailPopup(vehicleData = vehicle) {
                selectedVehicle = null
            }
        }
    }
}

@Composable
fun FleetVehicleItem(vehicleData: BmsData, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onItemClick).animateContentSize(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.DirectionsCar, contentDescription = "Vehicle", modifier = Modifier.size(40.dp))
            Text("Vehicle ID: ${vehicleData.vehicleId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Driver: ${vehicleData.driver ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

            // THE FIX #2: Call BatteryMeter with the correct parameter 'socPercent'
            BatteryMeter(
                socPercent = vehicleData.stateOfCharge,
                modifier = Modifier.height(12.dp).fillMaxWidth(0.7f)
            )
        }
    }
}

@Composable
fun FleetVehicleDetailPopup(vehicleData: BmsData, onDismiss: () -> Unit) {
    val chargeKwH = 50.0
    val rangeKm = (vehicleData.stateOfCharge / 100 * chargeKwH / 0.18).toInt()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f).wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // THE FIX #3: Convert vehicleId (Int) to String for the Text composable.
                Text(vehicleData.vehicleId.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                Text("BMS: ${vehicleData.bmsId}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SoCCircularProgressRing(socPercent = vehicleData.stateOfCharge, "SoC")
                    SoCCircularProgressRing(socPercent = vehicleData.stateOfHealth, "SoH")
                }
                Text("Estimated range: $rangeKm km", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp))
                Spacer(Modifier.height(12.dp))
                Text("Individual Cell Voltages", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    vehicleData.cellVoltages?.forEachIndexed { index, voltage ->
                        BatteryCellIcon(voltage = voltage, index = index)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Close") }
            }
        }
    }
}

@Composable
fun BatteryMeter(socPercent: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(socPercent / 100f)
                .fillMaxHeight()
                .background(
                    color = when {
                        socPercent < 20f -> Color.Red
                        socPercent <= 50f -> Color.Yellow
                        else -> Color(0xFF4CAF50)
                    }
                )
        )
    }
}

@Composable
fun SoCCircularProgressRing(socPercent: Float, label: String, modifier: Modifier = Modifier) {
    val socColor = when {
        socPercent < 20f -> Color.Red
        socPercent <= 50f -> Color.Yellow
        else -> Color(0xFF4CAF50)
    }
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(100.dp)) {
        CircularProgressIndicator(
            progress = socPercent / 100f,
            color = socColor,
            strokeWidth = 8.dp,
            modifier = Modifier.matchParentSize()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${socPercent.toInt()}%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun BatteryCellIcon(voltage: Float, index: Int) {
    val voltageColor = when {
        voltage < 3.2f -> Color.Red
        voltage < 3.6f -> Color.Yellow
        else -> Color(0xFF4CAF50)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(width = 30.dp, height = 50.dp).background(voltageColor, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text("C${index + 1}", style = MaterialTheme.typography.labelSmall)
        Text("%.2fV".format(voltage), style = MaterialTheme.typography.labelSmall)
    }
}
