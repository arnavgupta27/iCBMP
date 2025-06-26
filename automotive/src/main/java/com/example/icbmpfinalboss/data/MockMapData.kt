package com.example.icbmpfinalboss.data

import com.example.icbmpfinalboss.data.models.ChargingStation
import com.example.icbmpfinalboss.data.models.ChargingStationType
import com.example.icbmpfinalboss.data.models.Vehicle
import com.example.icbmpfinalboss.data.models.VehicleStatus

object MockMapData {

    val mockVehicles = listOf(
        Vehicle(
            id = "MH-001",
            latitude = 18.5204, // Pune City Centre - near Shivajinagar [4]
            longitude = 73.8567,
            currentSoc = 75,
            status = VehicleStatus.Driving,
            model = "XUV400" // [3][5]
        ),
        Vehicle(
            id = "MH-002",
            latitude = 18.5270, // Near Pimpri-Chinchwad (Westend Mall area) [2]
            longitude = 73.8020,
            currentSoc = 92,
            status = VehicleStatus.Idle,
            model = "Scorpio" // Older Mahindra EV
        ),
        Vehicle(
            id = "MH-003",
            latitude = 18.5600, // Near Khadki / Old Pune Mumbai Highway
            longitude = 73.8750,
            currentSoc = 30,
            status = VehicleStatus.Charging,
            model = "evBolero" // Futuristic/Concept Mahindra EV
        ),
        Vehicle(
            id = "MH-004",
            latitude = 18.4900, // Near Swargate / Southern Pune [1]
            longitude = 73.8650,
            currentSoc = 5,
            status = VehicleStatus.Offline,
            model = "KUV" // Another Mahindra EV
        )
    )

    val mockChargingStations = listOf(
        ChargingStation(
            id = "PNC-001",
            latitude = 18.5205, // Bafna Motors Showroom Charging Station, Law College Road [4]
            longitude = 73.8449,
            name = "Station 1",
            availableConnectors = 4,
            totalConnectors = 5,
            type = ChargingStationType.DCFast
        ),
        ChargingStation(
            id = "PNC-002",
            latitude = 18.5279, // Nexus Westend Mall Charging Station [2]
            longitude = 73.8055,
            name = "Station 2",
            availableConnectors = 2,
            totalConnectors = 3,
            type = ChargingStationType.Fast
        ),
        ChargingStation(
            id = "PNC-003",
            latitude = 18.5200, // Statiq Charging Hub (example near Shivajinagar) [2]
            longitude = 73.8550,
            name = "Station 3",
            availableConnectors = 5,
            totalConnectors = 8,
            type = ChargingStationType.DCFast
        )
    )
}



