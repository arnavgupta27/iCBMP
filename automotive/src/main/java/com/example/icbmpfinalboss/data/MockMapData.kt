package com.example.icbmpfinalboss.data

import com.example.icbmpfinalboss.data.models.ChargingStation
import com.example.icbmpfinalboss.data.models.ChargingStationType
import com.example.icbmpfinalboss.data.models.Vehicle
import com.example.icbmpfinalboss.data.models.VehicleStatus

// mock map data for vehicles and charging station
object MockMapData {

    val mockVehicles = listOf(
        Vehicle(
            id = "MH-001",
            latitude = 18.5204,
            longitude = 73.8567,
            currentSoc = 75,
            status = VehicleStatus.Driving,
            model = "XUV400"
        ),
        Vehicle(
            id = "MH-002",
            latitude = 18.5270,
            longitude = 73.8020,
            currentSoc = 92,
            status = VehicleStatus.Idle,
            model = "Scorpio"
        ),
        Vehicle(
            id = "MH-003",
            latitude = 18.5600,
            longitude = 73.8750,
            currentSoc = 30,
            status = VehicleStatus.Charging,
            model = "evBolero"
        ),
        Vehicle(
            id = "MH-004",
            latitude = 18.4900,
            longitude = 73.8650,
            currentSoc = 5,
            status = VehicleStatus.Offline,
            model = "KUV"
        )
    )

    val mockChargingStations = listOf(
        ChargingStation(
            id = "PNC-001",
            latitude = 18.5205,
            longitude = 73.8449,
            name = "Station 1",
            availableConnectors = 4,
            totalConnectors = 5,
            type = ChargingStationType.DCFast
        ),
        ChargingStation(
            id = "PNC-002",
            latitude = 18.5279,
            longitude = 73.8055,
            name = "Station 2",
            availableConnectors = 2,
            totalConnectors = 3,
            type = ChargingStationType.Fast
        ),
        ChargingStation(
            id = "PNC-003",
            latitude = 18.5200,
            longitude = 73.8550,
            name = "Station 3",
            availableConnectors = 5,
            totalConnectors = 8,
            type = ChargingStationType.DCFast
        )
    )
}



