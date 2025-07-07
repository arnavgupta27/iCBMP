// File: app/src/main/java/com/example/icbmpfinalboss/data/models/Prediction.kt

package com.example.icbmpfinalboss.data.models

import com.google.gson.annotations.SerializedName

// This class perfectly matches the structure of the JSON from the new endpoint.
data class Prediction(
    @SerializedName("prediction_id")
    val predictionId: String,

    @SerializedName("vehicle_id")
    val vehicleId: Int,

    @SerializedName("prediction_status")
    val predictionStatus: String, // This replaces "severity"

    @SerializedName("probability")
    val probability: Probability, // This is a nested object

    @SerializedName("recommendation")
    val recommendation: String, // This replaces "description"

    @SerializedName("created_at")
    val createdAt: String
)

// This class matches the nested "probability" object in the JSON.
data class Probability(
    @SerializedName("Healthy")
    val healthy: Float,

    @SerializedName("Warning")
    val warning: Float,

    @SerializedName("Critical")
    val critical: Float
)


