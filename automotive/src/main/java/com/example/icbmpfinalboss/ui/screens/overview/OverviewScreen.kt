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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp

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
import com.example.icbmpfinalboss.data.models.BmsData
import com.example.icbmpfinalboss.viewmodel.BmsViewModel

import androidx.compose.material3.Card // Ensure this import is there
import androidx.compose.material3.CardDefaults // Ensure this import is there
import androidx.compose.material3.Icon // Ensure this import is there
import androidx.compose.material3.Text // Ensure thi
import androidx.compose.ui.geometry.Offset

import androidx.compose.foundation.layout.IntrinsicSize // NEW Import

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

//        floatingActionButton = {
//            // Refresh button (data simulation auto-refreshes, but this shows pattern)
//            FloatingActionButton(onClick = { /* BmsViewModel.fetchBmsData() if not auto-refreshing */ }) {
//                Icon(Icons.Filled.Refresh, contentDescription = "Refresh Data")
//            }
//        }
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
        }

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

@Composable
fun BatterySummaryCard(data: BmsData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(IntrinsicSize.Min), // Ensures the Row's height is the height of its tallest child
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Each card gets a weight of 1f and fills the max height available to it (the Row's height)
        BatteryMetricCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), // Make each card fill the height of the Row
            title = "Avg SoC",
            value = "78",
            unit = "%",
            trend = 2.5f,
            statusColor = Color(0xFF4CAF50),
            progress = 0.78f
        )
        BatteryMetricCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            title = "Avg SoH",
            value = "95",
            unit = "%",
            trend = -0.2f,
            statusColor = Color(0xFF4CAF50),
            progress = 0.95f
        )
        BatteryMetricCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            title = "Temp",
            value = "35.2",
            unit = "°C",
            trend = 1.1f,
            statusColor = Color(0xFFFFC107)
        )
        BatteryMetricCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            title = "Voltage",
            value = "380",
            unit = "V",
            statusColor = Color(0xFF2196F3)
        )
        BatteryMetricCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            title = "Current",
            value = "50",
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


@Composable
fun BatteryMetricCard(
    modifier: Modifier = Modifier, // Add modifier parameter
    title: String,
    value: String,
    unit: String,
    trend: Float = 0f,
    statusColor: Color = MaterialTheme.colorScheme.primary,
    progress: Float? = null
) {
    Card(
        // The modifier will be passed from the parent Row (e.g., with weight)
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp) // Reduced spacing
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium, // Smaller font
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall, // Smaller font
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall, // Smaller font
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp) // Adjust alignment
                )
            }

            // Trend Indicator (no changes needed here, it's already compact)
            if (trend != 0f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val trendColor = if (trend > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    val trendIcon = if (trend > 0) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
                    Icon(
                        imageVector = trendIcon,
                        contentDescription = null,
                        tint = trendColor,
                        modifier = Modifier.size(18.dp) // Slightly smaller icon
                    )
                    Text(
                        text = "${"%.1f".format(kotlin.math.abs(trend))}%",
                        style = MaterialTheme.typography.labelSmall, // Smaller font
                        color = trendColor
                    )
                }
            }

            // Progress Bar (no changes needed here)
            if (progress != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp) // Thinner bar
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
