// File: app/src/main/java/com/example/icbmpfinalboss/data/models/BmsData.kt

package com.example.icbmpfinalboss.data.models

import com.google.gson.annotations.SerializedName

// This class now perfectly matches the keys from the API's JSON response.
data class BmsData(
    @SerializedName("bms_id")
    val bmsId: String,

    @SerializedName("vehicleid")
    val vehicleId: Int,

    @SerializedName("state_of_charge")
    val stateOfCharge: Float,

    @SerializedName("state_of_health")
    val stateOfHealth: Float,

    @SerializedName("total_pack_voltage")
    val voltage: Float,

    @SerializedName("total_pack_current")
    val current: Float,

    @SerializedName("max_cell_temp")
    val temperature: Float,

    // THE FIX: We add the 'driver' field back, but make it nullable ('String?').
    // Since the API doesn't send it, this field will be null, and the app won't crash during parsing.
    @SerializedName("driver")
    val driver: String? = null,

    // These fields are not in the current API response, so we keep them nullable.
    @SerializedName("cellVoltages")
    val cellVoltages: List<Float>? = null,

    @SerializedName("socHistory")
    val socHistory: List<Float>? = null
)
