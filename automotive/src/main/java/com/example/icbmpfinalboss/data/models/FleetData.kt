package com.example.icbmpfinalboss.data.models

import kotlin.random.Random

data class Car(
    val id: String,
    val name: String,
    val plateNumber: String,
    val driver: Driver,
    val alerts: List<CarAlert> = emptyList(),
    val stateOfCharge: Float,
    val chargeKwH: Float,
    val locationCity: String,
    val locationLocality: String,
    val batteryCellCharges: List<Float>,
    val stateOfHealth: Float = 0.9f // Default SoH value, adjust as needed in your data
)

data class Driver(
    val id: String,
    val name: String
)

data class CarAlert(
    val id: String,
    val description: String,
    val severity: AlertSeverity = AlertSeverity.LOW
)

enum class AlertSeverity {
    LOW, MEDIUM, HIGH
}

object SampleFleetData {
    val drivers = listOf(
        Driver(id = "d1", name = "Alice Smith"),
        Driver(id = "d2", name = "Bob Johnson"),
        Driver(id = "d3", name = "Carol Williams"),
        Driver(id = "d4", name = "Dave Brown"),
        Driver(id = "d5", name = "Eve Davis"),
        Driver(id = "d6", name = "Frank Miller")
    )

    val cars = listOf(
        Car(
            id = "c1", name = "Sedan Alpha", plateNumber = "AB 123 CD", driver = drivers[0],
            alerts = listOf(CarAlert(id = "a1", description = "Low tire pressure")),
            stateOfCharge = 0.75f, chargeKwH = 45.0f,
            locationCity = "Berlin", locationLocality = "Mitte",
            batteryCellCharges = List(12) { (0.80f..0.95f).random() }
        ),
        Car(
            id = "c2", name = "SUV Beta", plateNumber = "EF 456 GH", driver = drivers[1],
            alerts = emptyList(), stateOfCharge = 0.90f, chargeKwH = 60.5f,
            locationCity = "Munich", locationLocality = "Schwabing",
            batteryCellCharges = List(16) { (0.85f..0.98f).random() }
        ),
        Car(
            id = "c3", name = "Hatchback Gamma", plateNumber = "IJ 789 KL", driver = drivers[2],
            alerts = listOf(
                CarAlert(id = "a2", description = "Service due"),
                CarAlert(id = "a3", description = "Battery cooling issue", severity = AlertSeverity.HIGH)
            ),
            stateOfCharge = 0.50f, chargeKwH = 30.2f,
            locationCity = "Hamburg", locationLocality = "Altona",
            batteryCellCharges = List(12) { (0.70f..0.90f).random() }
        ),
        Car(
            id = "c4", name = "Van Delta", plateNumber = "MN 101 OP", driver = drivers[3],
            alerts = emptyList(), stateOfCharge = 0.85f, chargeKwH = 55.0f,
            locationCity = "Frankfurt", locationLocality = "Innenstadt",
            batteryCellCharges = List(16) { (0.80f..0.95f).random() }
        ),
        Car(
            id = "c5", name = "Pickup Epsilon", plateNumber = "QR 202 ST", driver = drivers[4],
            alerts = listOf(CarAlert(id = "a4", description = "Engine warning")),
            stateOfCharge = 0.60f, chargeKwH = 40.0f,
            locationCity = "Cologne", locationLocality = "Altstadt",
            batteryCellCharges = List(16) { (0.70f..0.95f).random() }
        ),
        Car(
            id = "c6", name = "Sedan Zeta", plateNumber = "UV 303 WX", driver = drivers[5],
            alerts = emptyList(), stateOfCharge = 0.95f, chargeKwH = 65.0f,
            locationCity = "Stuttgart", locationLocality = "Mitte",
            batteryCellCharges = List(12) { (0.90f..1.0f).random() }
        )
    )

    private fun ClosedRange<Float>.random() = Random.nextFloat() * (endInclusive - start) + start
}
