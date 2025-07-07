package com.example.icbmpfinalboss.data.models

data class ConvoyRecommendation(
    val reason: String,
    val recommended_speed_kmph: Int,
    val action: String,
    val vehicle_id: Int
)
