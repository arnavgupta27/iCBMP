
package com.example.icbmpfinalboss.data.models

import com.google.gson.annotations.SerializedName

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

    // NEW & CRUCIAL: We need the timestamp from the API to sort the data.
    @SerializedName("created_at")
    val createdAt: String,

    // This field remains nullable as the API doesn't send it.
    @SerializedName("driver")
    val driver: String? = null,

    // This field also remains nullable.
    @SerializedName("cellVoltages")
    val cellVoltages: List<Float>? = null,

    // UPDATED to var: This allows our ViewModel to calculate and then attach the
    // history to this object after it's been created from the JSON.
    var socHistory: List<Float>? = null,
    var sohHistory: List<Float>? = null
)
