package com.example.icbmpfinalboss.data.models


data class Vehicle(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val currentSoc: Int, // State of Charge (0-100%)
    val status: VehicleStatus = VehicleStatus.Idle,
    val model: String = "Generic EV"
)

enum class VehicleStatus {
    Idle, Driving, Charging, Maintenance, Offline
}

data class ChargingStation(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val availableConnectors: Int,
    val totalConnectors: Int,
    val type: ChargingStationType = ChargingStationType.Fast
)

enum class ChargingStationType {
    Fast, Standard, DCFast
}
