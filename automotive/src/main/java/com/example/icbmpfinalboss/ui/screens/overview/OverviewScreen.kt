package com.example.icbmpfinalboss.ui.screens.overview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icbmpfinalboss.data.models.Prediction
import com.example.icbmpfinalboss.data.models.BmsData
import com.example.icbmpfinalboss.viewmodel.PredictionsUiState
import com.example.icbmpfinalboss.viewmodel.BmsUiState
import com.example.icbmpfinalboss.viewmodel.FleetListUiState
import com.example.icbmpfinalboss.viewmodel.FleetViewModel
import com.example.icbmpfinalboss.viewmodel.ConvoyViewModel
import com.example.icbmpfinalboss.viewmodel.ConvoyViewModelFactory
import com.example.icbmpfinalboss.data.network.ConvoyRepository
import com.example.icbmpfinalboss.data.network.ConvoyRetrofitProvider
import com.example.icbmpfinalboss.ui.components.ConvoyNotification
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.*
import androidx.compose.runtime.*

import java.time.Instant

import java.time.ZoneId



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    fleetViewModel: FleetViewModel = viewModel()
) {
    val uiState by fleetViewModel.overviewState.collectAsState()
    val fleetState by fleetViewModel.fleetListState.collectAsState()
    val predictionsState by fleetViewModel.predictionsState.collectAsState()

    // Convoy ViewModel setup
    val convoyFactory = remember {
        ConvoyViewModelFactory(
            ConvoyRepository(ConvoyRetrofitProvider.convoyApiService)
        )
    }
    val convoyViewModel: ConvoyViewModel = viewModel(factory = convoyFactory)

    LaunchedEffect(Unit) {
        convoyViewModel.fetchConvoyData()
    }
    val convoyRecommendations by convoyViewModel.convoyRecommendations.collectAsState()
    val convoyPredictions by convoyViewModel.convoyPredictions.collectAsState()

    var showLeaveDialog by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Convoy notification banners
            val topRec = convoyRecommendations.firstOrNull()
            val topPred = convoyPredictions.firstOrNull()
            if (topRec != null) {
                ConvoyNotification(
                    title = "Convoy Recommendation",
                    message = "${topRec.reason} (Vehicle ${topRec.vehicle_id})",
                    onDismiss = { convoyViewModel.acknowledgeConvoyRecommendation(topRec.vehicle_id) }
                )
            }
            if (topPred != null) {
                ConvoyNotification(
                    title = "Convoy Prediction",
                    message = "Predicted battery used: ${topPred.predicted_batteryused_kwh} kWh (Vehicle ${topPred.vehicle_id})",
                    onDismiss = { convoyViewModel.acknowledgeConvoyPrediction(topPred.vehicle_id) }
                )
            }

            // Top Row: Vehicle Picker | Notify Leave Button | Prediction Center
            if (uiState is BmsUiState.Success) {
                val state = uiState as BmsUiState.Success
                val fullFleet = (fleetState as? FleetListUiState.Success)?.fleet ?: emptyList()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vehicle Picker (left)
                    Box(modifier = Modifier.weight(1.5f)) {
                        var isDropdownExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            TextField(
                                value = "Vehicle ID: ${state.data.vehicleId}",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                modifier = Modifier.menuAnchor(),
                                colors = ExposedDropdownMenuDefaults.textFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                fullFleet.forEach { vehicle ->
                                    DropdownMenuItem(
                                        text = { Text("Vehicle ID: ${vehicle.vehicleId}") },
                                        onClick = {
                                            fleetViewModel.selectVehicleForOverview(vehicle.vehicleId)
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Notify Leave Button (center)
                    Button(
                        onClick = { showLeaveDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f)
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Notify Leave")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Prediction Center (right)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        PredictionCenter(
                            predictionsState = predictionsState,
                            onAcknowledge = { fleetViewModel.acknowledgePrediction(it) },
                            onClearAll = { fleetViewModel.clearNonWarningPredictions() }
                        )
                    }
                }
            }

            // Leave Notification Dialog
            if (showLeaveDialog) {
                LeaveNotificationDialog(
                    onDismiss = { showLeaveDialog = false }
                )
            }

            // Dashboard content
            Box(
                modifier = Modifier.fillMaxSize(),
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
                            predictionsState = predictionsState,
                            onAcknowledgePrediction = { fleetViewModel.acknowledgePrediction(it) },
                            onClearAllPredictions = { fleetViewModel.clearNonWarningPredictions() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveNotificationDialog(
    onDismiss: () -> Unit
) {
    var fromDate by remember { mutableStateOf<LocalDate?>(null) }
    var toDate by remember { mutableStateOf<LocalDate?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    "Notify Supervisor: Battery Idle Period",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Set the dates for your planned leave. Your supervisor and the BMS will be notified that your battery will be idle during this period.",
                    style = MaterialTheme.typography.bodyMedium
                )
                DateRangePickerField(
                    fromDate = fromDate,
                    toDate = toDate,
                    onDateRangeSelected = { from, to ->
                        fromDate = from
                        toDate = to
                    }
                )
                Button(
                    onClick = { onDismiss() }, // No backend yet
                    enabled = fromDate != null && toDate != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Send Request")
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.toEpochDay()?.let { it * 24 * 60 * 60 * 1000 }
    )

    OutlinedTextField(
        value = selectedDate?.format(dateFormatter) ?: "",
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = modifier.clickable { showPicker = true }
    )

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            onDateSelected(date)
                        }
                        showPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    bmsData: BmsData,
    fullFleet: List<BmsData>,
    onVehicleSelected: (Int) -> Unit,
    predictionsState: PredictionsUiState,
    onAcknowledgePrediction: (String) -> Unit,
    onClearAllPredictions: () -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal= 30.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Vehicle Picker | Notify Leave Button | Prediction Center row is now in OverviewScreen
        BatterySummaryCard(data = bmsData)
        SocLineChart(history = bmsData.socHistory ?: emptyList())
        SoHLineChart(history = bmsData.sohHistory ?: emptyList())
    }
}

@Composable
fun PredictionCenter(
    predictionsState: PredictionsUiState,
    onAcknowledge: (String) -> Unit,
    onClearAll: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val predictionList = (predictionsState as? PredictionsUiState.Success)?.predictions ?: emptyList()

    Box(modifier = Modifier.padding(end = 35.dp)) {
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier.size(55.dp)
        ) {
            BadgedBox(
                badge = {
                    if (predictionList.isNotEmpty()) {
                        val badgeColor = if (predictionList.any { it.predictionStatus.equals("Critical", ignoreCase = true) }) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        Badge(containerColor = badgeColor) { Text(predictionList.size.toString()) }
                    }
                }
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Open Predictions",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
    if (showDialog) {
        PredictionsDialog(
            predictions = predictionList,
            onDismiss = { showDialog = false },
            onAcknowledge = onAcknowledge,
            onClearAll = onClearAll
        )
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
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Prediction Center", style = MaterialTheme.typography.headlineSmall)
                    TextButton(onClick = onClearAll) { Text("Clear Non-Warnings") }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                if (predictions.isEmpty()) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text("No active predictions.", style = MaterialTheme.typography.bodyLarge) }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = predictions, key = { it.predictionId }) { prediction ->
                            PredictionItem(
                                prediction = prediction,
                                onAcknowledge = { onAcknowledge(prediction.predictionId) }
                            )
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
        "warning" -> Color(0xFFFFE0B2)
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Vehicle ${prediction.vehicleId}: ${prediction.predictionStatus}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Recommendation: ${prediction.recommendation}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onAcknowledge) { Text("Acknowledge") }
            }
        }
    }
}

@Composable
fun BatterySummaryCard(data: BmsData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BatteryMetricCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            title = "SoC",
            value = "%.1f".format(data.stateOfCharge),
            unit = "%",
            statusColor = Color(0xFF4CAF50),
            progress = data.stateOfCharge / 100f
        )
        BatteryMetricCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            title = "SoH",
            value = "%.1f".format(data.stateOfHealth),
            unit = "%",
            statusColor = Color(0xFF4CAF50),
            progress = data.stateOfHealth / 100f
        )
        BatteryMetricCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            title = "Temp",
            value = "%.1f".format(data.temperature),
            unit = "°C",
            statusColor = Color(0xFFFFC107)
        )
        BatteryMetricCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            title = "Voltage",
            value = "%.1f".format(data.voltage),
            unit = "V",
            statusColor = Color(0xFF2196F3)
        )
        BatteryMetricCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            title = "Current",
            value = "%.1f".format(data.current),
            unit = "A",
            statusColor = Color(0xFFE91E63)
        )
    }
}

@Composable
fun SocLineChart(history: List<Float>) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("SOC History (%)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            ) {
                if (history.size > 1) {
                    val path = Path()
                    val minSoc = history.minOrNull() ?: 0f
                    val maxSoc = history.maxOrNull() ?: 100f
                    val socRange = (maxSoc - minSoc).coerceAtLeast(1f)
                    fun getYCoordinate(soc: Float): Float =
                        size.height - ((soc - minSoc) / socRange * size.height)
                    path.moveTo(0f, getYCoordinate(history.first()))
                    for (i in 1 until history.size) {
                        val x = size.width * (i.toFloat() / (history.size - 1))
                        val y = getYCoordinate(history[i])
                        path.lineTo(x, y)
                    }
                    drawPath(
                        path,
                        color = Color.Blue,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

@Composable
fun SoHLineChart(history: List<Float>) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("SoH History (%)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            ) {
                if (history.size > 1) {
                    val path = Path()
                    val minSoh = 80f
                    val maxSoh = 100f
                    val sohRange = maxSoh - minSoh
                    fun getYCoordinate(soh: Float): Float =
                        size.height - ((soh - minSoh) / sohRange * size.height)
                    path.moveTo(0f, getYCoordinate(history.first()))
                    for (i in 1 until history.size) {
                        val x = size.width * (i.toFloat() / (history.size - 1))
                        val y = getYCoordinate(history[i])
                        path.lineTo(x, y)
                    }
                    drawPath(
                        path,
                        color = Color(0xFF4CAF50),
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerField(
    fromDate: LocalDate?,
    toDate: LocalDate?,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val fromText = fromDate?.format(dateFormatter) ?: "From"
    val toText = toDate?.format(dateFormatter) ?: "To"

    OutlinedButton(
        onClick = { showPicker = true },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text("$fromText  —  $toText")
    }

    if (showPicker) {
        val pickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = fromDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            initialSelectedEndDateMillis = toDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val startMillis = pickerState.selectedStartDateMillis
                        val endMillis = pickerState.selectedEndDateMillis
                        if (startMillis != null && endMillis != null) {
                            val start = Instant.ofEpochMilli(startMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                            val end = Instant.ofEpochMilli(endMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                            onDateRangeSelected(start, end)
                        }
                        showPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            },
            text = {
                DateRangePicker(state = pickerState)
            }
        )
    }
}

@Composable
fun BatteryMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String,
    trend: Float = 0f,
    statusColor: Color = MaterialTheme.colorScheme.primary,
    progress: Float? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            // ... Trend and Progress Indicator logic ...
        }
    }
}

