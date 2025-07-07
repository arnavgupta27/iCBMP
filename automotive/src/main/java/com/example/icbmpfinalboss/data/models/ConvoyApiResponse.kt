package com.example.icbmpfinalboss.data.models

data class ConvoyApiResponse(
    val data: ConvoyData,
    val status: Int
)

data class ConvoyData(
    val convoy_recommendations: List<ConvoyRecommendation>,
    val predictions: List<PredictionNew>,
    val timestamp: String
)
