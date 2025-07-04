package com.example.icbmpfinalboss.data


import com.example.icbmpfinalboss.data.models.BmsData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

//for simulation of data for overview screen
class BmsRepository {

    private var socHistory = mutableListOf<Float>()

    fun getBmsDataStream(): Flow<BmsData> = flow {
        if (socHistory.isEmpty()) {
            socHistory.addAll(List(20) { 80f + Random.nextFloat() * 10 })
        }

        while (true) {
            emit(generateMockBmsData())
            delay(1000)
        }
    }

    // In BmsRepository.kt

    // In BmsRepositry.kt

    private fun generateMockBmsData(): BmsData {
        val lastSoc = socHistory.lastOrNull() ?: 85f
        val currentSoc = (lastSoc - Random.nextFloat() * 0.5f).coerceIn(30f, 95f)

        socHistory.add(currentSoc)
        if (socHistory.size > 30) {
            socHistory.removeAt(0)
        }

        return BmsData(
            // THE FIX #1: Added the missing bmsId parameter.
            bmsId = "simulated-bms-id",

            // THE FIX #2: Changed vehicleId from a String to an Int.
            vehicleId = 9999,

            // No changes needed for the rest of your simulation logic.
            driver = "Simulated Driver",
            stateOfCharge = currentSoc,
            stateOfHealth = 99.5f - (100f - currentSoc) / 15f,
            voltage = 380f + (currentSoc / 10f),
            current = -5.5f - Random.nextFloat() * 2,
            temperature = 31.5f + Random.nextFloat() * 3,
            cellVoltages = List(6) { 3.60f + Random.nextFloat() * 0.05f },
            socHistory = socHistory.toList()
        )
    }

}
