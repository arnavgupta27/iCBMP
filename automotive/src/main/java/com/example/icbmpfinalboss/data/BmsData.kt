package com.example.icbmpfinalboss.data

data class BmsData(
    val stateOfCharge: Float = 80.0f,
    val stateOfHealth: Float = 98.0f,
    val voltage: Float = 400.0f,
    val current: Float = -10.0f,
    val temperature: Float = 32.0f,
    val cellVoltages: List<Float> = List(6) { 3.65f },
    val socHistory: List<Float> = List(20) { 80.0f },
    val isChargingEnabled: Boolean = false,
    val isBalancingForced: Boolean = false
)
