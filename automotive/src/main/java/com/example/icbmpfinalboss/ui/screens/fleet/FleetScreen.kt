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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.icbmpfinalboss.data.models.Car
import com.example.icbmpfinalboss.data.models.SampleFleetData
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import kotlin.math.min

@Composable
fun CarGridItem(car: Car, onItemClick: (Car) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onItemClick(car) }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = car.name,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = car.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                BatteryMeter(
                    value = car.stateOfCharge,
                    modifier = Modifier.size(width = 60.dp, height = 16.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Driver: ${car.driver.name}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Plate: ${car.plateNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (car.alerts.isNotEmpty()) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerts",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BatteryMeter(value: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(value)
                .fillMaxHeight()
                .background(
                    color = when {
                        value < 0.2f -> Color.Red
                        value <= 0.5f -> Color.Yellow
                        else -> Color(0xFF4CAF50)
                    }
                )
        )
    }
}

@Composable
fun SoCCircularProgressRing(
    soc: Float,
    modifier: Modifier = Modifier
) {
    val socColor = when {
        soc < 0.2f -> Color.Red
        soc <= 0.5f -> Color.Yellow
        else -> Color(0xFF4CAF50)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(100.dp)
    ) {
        CircularProgressIndicator(
            progress = soc,
            color = socColor,
            strokeWidth = 8.dp,
            modifier = Modifier.matchParentSize()
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(soc * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BatteryCellIcon(
    soc: Float,
    index: Int
) {
    val socColor = when {
        soc < 0.2f -> Color.Red
        soc <= 0.5f -> Color.Yellow
        else -> Color(0xFF4CAF50)
    }
    val voltage = 3.0f + soc * 1.5f // Example voltage calculation

    Box(
        modifier = Modifier
            .size(64.dp)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Battery body
            drawRoundRect(
                color = Color.LightGray,
                topLeft = Offset(0f, 0f),
                size = Size(size.width * 0.85f, size.height),
                cornerRadius = CornerRadius(8f, 8f)
            )
            // Battery cap
            drawRect(
                color = Color.Gray,
                topLeft = Offset(size.width * 0.85f, size.height * 0.3f),
                size = Size(size.width * 0.15f, size.height * 0.4f)
            )
            // Diagonal fill
            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width * 0.85f, size.height)
                lineTo(size.width * 0.85f, size.height * (1 - soc))
                lineTo(0f, size.height * (1 - soc * 0.9f))
                close()
            }
            drawPath(path, color = socColor, style = Fill)
        }
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "C${index + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black
            )
            Text(
                text = "%.1fV".format(voltage),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black
            )
            Text(
                text = "${(soc * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CarDetailPopup(car: Car, onDismiss: () -> Unit) {
    val visibleAlerts = remember { mutableStateListOf(*car.alerts.toTypedArray()) }
    val rangeKm = (car.stateOfCharge * car.chargeKwH / 0.18f).toInt() // Example consumption

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Alert notifications (transparent red box, dismiss on tap)
                if (visibleAlerts.isNotEmpty()) {
                    Column {
                        visibleAlerts.forEach { alert ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Red.copy(alpha = 0.3f))
                                    .padding(8.dp)
                                    .clickable { visibleAlerts.remove(alert) }
                            ) {
                                Text(
                                    text = alert.description,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                Text(
                    text = car.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = car.plateNumber,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // Numeric SoC
                Text(
                    text = "SoC: ${(car.stateOfCharge * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // SoC Circular Progress Ring
                SoCCircularProgressRing(
                    soc = car.stateOfCharge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // SoH (add to Car model if not present)
                Text(
                    text = "SoH: ${(car.stateOfHealth * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Estimated range
                Text(
                    text = "Estimated range: $rangeKm km",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(12.dp))

                // Label for battery cells
                Text(
                    text = "Individual battery cell charges in ${car.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                // Battery cells
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    car.batteryCellCharges.forEachIndexed { index, soc ->
                        BatteryCellIcon(
                            soc = soc,
                            index = index
                        )
                    }
                    if (car.batteryCellCharges.size > 12) {
                        Text(
                            text = "...",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun FleetScreen() {
    val cars = SampleFleetData.cars
    var selectedCarForDetail by remember { mutableStateOf<Car?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        if (cars.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No cars in fleet.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cars, key = { car -> car.id }) { car ->
                    CarGridItem(car = car) {
                        selectedCarForDetail = it
                    }
                }
            }
        }
        selectedCarForDetail?.let { car ->
            CarDetailPopup(car = car) {
                selectedCarForDetail = null
            }
        }
    }
}
