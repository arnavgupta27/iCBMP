// File: app/src/main/java/com/example/icbmpfinalboss/ui/screens/overview/OverviewScreen.kt

package com.example.icbmpfinalboss.ui.screens.overview

// --- All necessary imports ---
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icbmpfinalboss.data.models.Prediction // Using the correct Prediction model
import com.example.icbmpfinalboss.data.models.BmsData
import com.example.icbmpfinalboss.viewmodel.PredictionsUiState // Using PredictionsUiState
import com.example.icbmpfinalboss.viewmodel.BmsUiState
import com.example.icbmpfinalboss.viewmodel.FleetListUiState
import com.example.icbmpfinalboss.viewmodel.FleetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    fleetViewModel: FleetViewModel = viewModel()
) {
    val uiState by fleetViewModel.overviewState.collectAsState()
    val fleetState by fleetViewModel.fleetListState.collectAsState()
    // --- RENAMED: We now observe the live predictionsState ---
    val predictionsState by fleetViewModel.predictionsState.collectAsState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is BmsUiState.Loading -> CircularProgressIndicator()
                is BmsUiState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                is BmsUiState.Success -> {
                    val fullFleet = (fleetState as? FleetListUiState.Success)?.fleet ?: emptyList()
                    DashboardContent(
                        bmsData = state.data,
                        fullFleet = fullFleet,
                        onVehicleSelected = { fleetViewModel.selectVehicleForOverview(it) },
                        // Pass the prediction state and functions
                        predictionsState = predictionsState,
                        onAcknowledgePrediction = { fleetViewModel.acknowledgePrediction(it) },
                        onClearAllPredictions = { fleetViewModel.clearNonWarningPredictions() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    bmsData: BmsData,
    fullFleet: List<BmsData>,
    onVehicleSelected: (Int) -> Unit,
    // RENAMED: Accepting prediction parameters
    predictionsState: PredictionsUiState,
    onAcknowledgePrediction: (String) -> Unit,
    onClearAllPredictions: () -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                TextField(value = "Vehicle ID: ${bmsData.vehicleId}", onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) }, modifier = Modifier.menuAnchor(), colors = ExposedDropdownMenuDefaults.textFieldColors())
                ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                    fullFleet.forEach { vehicle ->
                        DropdownMenuItem(text = { Text("Vehicle ID: ${vehicle.vehicleId}") }, onClick = { onVehicleSelected(vehicle.vehicleId); isDropdownExpanded = false })
                    }
                }
            }
            // Pass the new state and actions down to PredictionCenter
            PredictionCenter(
                predictionsState = predictionsState,
                onAcknowledge = onAcknowledgePrediction,
                onClearAll = onClearAllPredictions
            )
        }
        BatterySummaryCard(data = bmsData)
        SocLineChart(history = bmsData.socHistory ?: emptyList())
        SoHLineChart(history = bmsData.sohHistory ?: emptyList())
    }
}

// --- RENAMED "ALERT" TO "PREDICTION" EVERYWHERE ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionCenter(
    predictionsState: PredictionsUiState,
    onAcknowledge: (String) -> Unit,
    onClearAll: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val predictionList = (predictionsState as? PredictionsUiState.Success)?.predictions ?: emptyList()

    Box {
        IconButton(onClick = { showDialog = true }) {
            BadgedBox(
                badge = {
                    if (predictionList.isNotEmpty()) {
                        val badgeColor = if (predictionList.any { it.predictionStatus.equals("Critical", ignoreCase = true) }) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        Badge(containerColor = badgeColor) { Text(predictionList.size.toString()) }
                    }
                }
            ) { Icon(Icons.Default.Notifications, contentDescription = "Open Predictions") }
        }
    }
    if (showDialog) {
        PredictionsDialog(predictions = predictionList, onDismiss = { showDialog = false }, onAcknowledge = onAcknowledge, onClearAll = onClearAll)
    }
}

@Composable
fun PredictionsDialog(
    predictions: List<Prediction>,
    onDismiss: () -> Unit,
    onAcknowledge: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Prediction Center", style = MaterialTheme.typography.headlineSmall)
                    TextButton(onClick = onClearAll) { Text("Clear Non-Warnings") }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                if (predictions.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No active predictions.", style = MaterialTheme.typography.bodyLarge) }
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(items = predictions, key = { it.predictionId }) { prediction ->
                            PredictionItem(prediction = prediction, onAcknowledge = { onAcknowledge(prediction.predictionId) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionItem(
    prediction: Prediction,
    onAcknowledge: () -> Unit
) {
    val cardColor = when (prediction.predictionStatus.lowercase()) {
        "critical" -> MaterialTheme.colorScheme.errorContainer
        "warning" -> Color(0xFFFFE0B2) // Light Orange
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = cardColor), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Vehicle ${prediction.vehicleId}: ${prediction.predictionStatus}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "Recommendation: ${prediction.recommendation}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onAcknowledge) { Text("Acknowledge") }
            }
        }
    }
}

// --- The rest of your composables (no changes needed) ---

@Composable
fun BatterySummaryCard(data: BmsData) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        BatteryMetricCard(modifier = Modifier.weight(1f).fillMaxHeight(), title = "SoC", value = "%.1f".format(data.stateOfCharge), unit = "%", statusColor = Color(0xFF4CAF50), progress = data.stateOfCharge / 100f)
        BatteryMetricCard(modifier = Modifier.weight(1f).fillMaxHeight(), title = "SoH", value = "%.1f".format(data.stateOfHealth), unit = "%", statusColor = Color(0xFF4CAF50), progress = data.stateOfHealth / 100f)
        BatteryMetricCard(modifier = Modifier.weight(1f).fillMaxHeight(), title = "Temp", value = "%.1f".format(data.temperature), unit = "°C", statusColor = Color(0xFFFFC107))
        BatteryMetricCard(modifier = Modifier.weight(1f).fillMaxHeight(), title = "Voltage", value = "%.1f".format(data.voltage), unit = "V", statusColor = Color(0xFF2196F3))
        BatteryMetricCard(modifier = Modifier.weight(1f).fillMaxHeight(), title = "Current", value = "%.1f".format(data.current), unit = "A", statusColor = Color(0xFFE91E63))
    }
}

@Composable
fun SocLineChart(history: List<Float>) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth().height(180.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("SOC History (%)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Canvas(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp)) {
                if (history.size > 1) {
                    val path = Path()
                    val minSoc = history.minOrNull() ?: 0f
                    val maxSoc = history.maxOrNull() ?: 100f
                    val socRange = (maxSoc - minSoc).coerceAtLeast(1f)
                    fun getYCoordinate(soc: Float): Float = size.height - ((soc - minSoc) / socRange * size.height)
                    path.moveTo(0f, getYCoordinate(history.first()))
                    for (i in 1 until history.size) {
                        val x = size.width * (i.toFloat() / (history.size - 1))
                        val y = getYCoordinate(history[i])
                        path.lineTo(x, y)
                    }
                    drawPath(path, color = Color.Blue, style = Stroke(width = 4f, cap = StrokeCap.Round))
                }
            }
        }
    }
}

@Composable
fun SoHLineChart(history: List<Float>) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth().height(180.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("SoH History (%)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Canvas(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp)) {
                if (history.size > 1) {
                    val path = Path()
                    val minSoh = history.minOrNull()?.coerceAtLeast(80f) ?: 80f
                    val maxSoh = 100f
                    val sohRange = (maxSoh - minSoh).coerceAtLeast(1f)
                    fun getYCoordinate(soh: Float): Float = size.height - ((soh - minSoh) / sohRange * size.height)
                    path.moveTo(0f, getYCoordinate(history.first()))
                    for (i in 1 until history.size) {
                        val x = size.width * (i.toFloat() / (history.size - 1))
                        val y = getYCoordinate(history[i])
                        path.lineTo(x, y)
                    }
                    drawPath(path, color = Color(0xFF4CAF50), style = Stroke(width = 4f, cap = StrokeCap.Round))
                }
            }
        }
    }
}

@Composable
fun BatteryMetricCard(modifier: Modifier = Modifier, title: String, value: String, unit: String, trend: Float = 0f, statusColor: Color = MaterialTheme.colorScheme.primary, progress: Float? = null) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
            }
            // ... Trend and Progress Indicator logic ...
        }
    }
}
