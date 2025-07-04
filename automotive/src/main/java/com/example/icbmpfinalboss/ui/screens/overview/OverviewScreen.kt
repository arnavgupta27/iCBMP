// File: app/src/main/java/com/example/icbmpfinalboss/ui/screens/overview/OverviewScreen.kt

package com.example.icbmpfinalboss.ui.screens.overview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.icbmpfinalboss.data.models.BmsData
import com.example.icbmpfinalboss.viewmodel.BmsUiState // IMPORTANT: Import the new state class
import com.example.icbmpfinalboss.viewmodel.FleetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    fleetViewModel: FleetViewModel = viewModel()
) {
    val uiState by fleetViewModel.overviewState.collectAsState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is BmsUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is BmsUiState.Error -> {
                    Text(
                        text = "Failed to load data: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is BmsUiState.Success -> {
                    DashboardContent(bmsData = state.data)
                }
            }
        }
    }
}

@Composable
fun DashboardContent(bmsData: BmsData) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dashboard Overview", style = MaterialTheme.typography.headlineMedium)
            AlertCenter()
        }
        BatterySummaryCard(data = bmsData)
        SocLineChart(history = bmsData.socHistory ?: emptyList())
        CellVoltageBarChart(cellVoltages = bmsData.cellVoltages?: emptyList())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertCenter() {
    var showDialog by remember { mutableStateOf(false) }
    val notifications = remember {
        mutableStateListOf(
            "High Temperature Warning: Cell Block A",
            "Firmware Update v1.9.2 Available",
            "Critical: SoC below 15%",
            "Cell Imbalance Detected: Group 3",
            "Connection Lost: Vehicle #1024"
        )
    }

    Box {
        IconButton(onClick = { showDialog = true }) {
            BadgedBox(
                badge = {
                    if (notifications.isNotEmpty()) {
                        Badge { Text(notifications.size.toString()) }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Open Alerts",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    if (showDialog) {
        AlertsDialog(
            notifications = notifications,
            onDismiss = { showDialog = false },
            onClear = { notification -> notifications.remove(notification) },
            onAcknowledge = { notification ->
                notifications.remove(notification)
            }
        )
    }
}

// THIS IS THE FIX. Note that 'List' is now 'List<String>'.
@Composable
fun AlertsDialog(
    notifications: List<String>, // CORRECTED: Was 'List', now 'List<String>'
    onDismiss: () -> Unit,
    onAcknowledge: (String) -> Unit,
    onClear: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Alert Center", style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Alerts")
                    }
                }
                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("All alerts cleared!", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Because the list type is now known, this 'items' block will no longer have errors.
                        items(items = notifications, key = { it }) { notification ->
                            NotificationItem(
                                notificationText = notification,
                                onAcknowledge = { onAcknowledge(notification) },
                                onClear = { onClear(notification) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notificationText: String,
    onAcknowledge: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = notificationText,
                style = MaterialTheme.typography.bodyLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onAcknowledge) {
                    Text("Acknowledge")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onClear) {
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
fun BatterySummaryCard(data: BmsData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BatteryMetricCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            title = "Avg SoC",
            // CORRECTED: Use data.stateOfCharge
            value = "%.1f".format(data.stateOfCharge),
            unit = "%",
            statusColor = Color(0xFF4CAF50),
            progress = data.stateOfCharge / 100f
        )
        BatteryMetricCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            title = "Avg SoH",
            // CORRECTED: Use data.stateOfHealth
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
fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = label, style = MaterialTheme.typography.labelMedium)
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
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp)
            ) {
                if (history.size > 1) {
                    val path = Path()
                    val minSoc = history.minOrNull() ?: 0f
                    val maxSoc = history.maxOrNull() ?: 100f
                    val socRange = (maxSoc - minSoc).coerceAtLeast(1f)

                    fun getYCoordinate(soc: Float): Float {
                        return size.height - ((soc - minSoc) / socRange * size.height)
                    }

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
fun CellVoltageBarChart(cellVoltages: List<Float>) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Cell Voltages (V)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom,
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                val minVoltage = cellVoltages.minOrNull() ?: 3.0f
                val maxVoltage = cellVoltages.maxOrNull() ?: 4.2f
                val voltageRange = (maxVoltage - minVoltage).coerceAtLeast(0.1f)

                itemsIndexed(cellVoltages) { index, voltage ->
                    val barHeightFactor = ((voltage - minVoltage) / voltageRange).coerceIn(0f, 1f)
                    val animatedHeight by animateFloatAsState(
                        targetValue = barHeightFactor,
                        animationSpec = tween(durationMillis = 500)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight().width(40.dp)
                    ) {
                        Text(
                            text = "%.2f".format(voltage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .fillMaxHeight(animatedHeight)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Cell ${index + 1}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
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
            if (trend != 0f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val trendColor = if (trend > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    val trendIcon = if (trend > 0) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
                    Icon(
                        imageVector = trendIcon,
                        contentDescription = null,
                        tint = trendColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${"%.1f".format(kotlin.math.abs(trend))}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = trendColor
                    )
                }
            }
            if (progress != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Canvas(
                    modifier = Modifier.fillMaxWidth().height(4.dp)
                ) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = size.height,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = statusColor,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width * progress, size.height / 2),
                        strokeWidth = size.height,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
