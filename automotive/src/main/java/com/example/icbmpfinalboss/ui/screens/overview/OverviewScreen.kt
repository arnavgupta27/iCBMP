package com.example.icbmpfinalboss.ui.screens.overview // Ensure your package name is correct

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icbmpfinalboss.data.BmsData
import com.example.icbmpfinalboss.viewmodel.BmsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    bmsViewModel: BmsViewModel = viewModel()
) {
    val bmsData by bmsViewModel.bmsState.collectAsState()
    val isChargingEnabled by bmsViewModel.isChargingEnabled.collectAsState()
    val isBalancingForced by bmsViewModel.isBalancingForced.collectAsState()

    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloud BMS Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    // Example action button
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Filled.Info, contentDescription = "App Info")
                    }
                }
            )
        },
        floatingActionButton = {
            // Refresh button (data simulation auto-refreshes, but this shows pattern)
            FloatingActionButton(onClick = { /* BmsViewModel.fetchBmsData() if not auto-refreshing */ }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh Data")
            }
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Spacing between cards/sections
        ) {
            // Battery Summary Card
            BatterySummaryCard(data = bmsData)

            // SOC Line Chart
            SocLineChart(history = bmsData.socHistory)

            // Cell Voltage Bar Chart
            CellVoltageBarChart(cellVoltages = bmsData.cellVoltages)

            // Control Panel for toggles
            ControlPanel(
                isChargingEnabled = isChargingEnabled,
                isBalancingForced = isBalancingForced,
                onChargingChanged = { bmsViewModel.setCharging(it) },
                onBalancingChanged = { bmsViewModel.setBalancing(it) }
            )
        }

        // --- Example of a Composable controlled by state ---
        // This AlertDialog is only composed when showInfoDialog is true.
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = { Text("App Information") },
                text = { Text("This is a simulated Battery Management System (BMS) Dashboard. Data updates in real-time.") },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

/**
 * Displays key battery metrics.
 */
@Composable
fun BatterySummaryCard(data: BmsData) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Battery Summary", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                SummaryItem("SOC", "%.1f%%".format(data.stateOfCharge))
                SummaryItem("Voltage", "%.1fV".format(data.voltage))
                SummaryItem("Current", "%.1fA".format(data.current))
                SummaryItem("Temp", "%.1f°C".format(data.temperature))
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

/**
 * Draws a line chart for the State of Charge history.
 */
@Composable
fun SocLineChart(history: List<Float>) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // Increased height for better visualization
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
                    // Adjust min/max for realistic SOC range
                    val minSoc = 30f // Assuming SOC won't go below this in the short term
                    val maxSoc = 100f
                    val socRange = (maxSoc - minSoc).coerceAtLeast(1f) // Avoid division by zero

                    // Scale Y-axis to canvas height
                    fun getYCoordinate(soc: Float): Float {
                        return size.height - ((soc - minSoc) / socRange * size.height)
                    }

                    // Move to the starting point
                    path.moveTo(0f, getYCoordinate(history.first()))

                    // Draw lines to subsequent points
                    for (i in 1 until history.size) {
                        val x = size.width * (i.toFloat() / (history.size - 1))
                        val y = getYCoordinate(history[i])
                        path.lineTo(x, y)
                    }

                    drawPath(
                        path,
                        color = Color.Blue, // Using theme primary color
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
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
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // Consistent height with line chart
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Cell Voltages (V)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom, // Bars start from bottom
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                itemsIndexed(cellVoltages) { index, voltage ->
                    // Animate bar height for a smoother update effect
                    val animatedHeight by animateFloatAsState(
                        targetValue = (voltage - 3.5f).coerceAtLeast(0f) * 400, // Scale factor
                        animationSpec = tween(durationMillis = 500)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxHeight() // Allows bars to take up full height of LazyRow
                            .width(40.dp)
                    ) {
                        // Display voltage value above the bar
                        Text(
                            text = "%.2f".format(voltage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Text color
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(animatedHeight.dp) // Use animated height
                                .background(MaterialTheme.colorScheme.secondary) // Using theme secondary color
                        )
                        Spacer(Modifier.height(4.dp)) // Space between bar and label
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

/**
 * Provides toggle switches for basic BMS controls.
 */
@Composable
fun ControlPanel(
    isChargingEnabled: Boolean,
    isBalancingForced: Boolean,
    onChargingChanged: (Boolean) -> Unit,
    onBalancingChanged: (Boolean) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Control Panel", style = MaterialTheme.typography.titleLarge)
            Divider(modifier = Modifier.padding(vertical = 8.dp)) // Visual separator

            // Enable Charging Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable Charging", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = isChargingEnabled, onCheckedChange = onChargingChanged)
            }

            // Force Balancing Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Force Balancing", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = isBalancingForced, onCheckedChange = onBalancingChanged)
            }
        }
    }
}
