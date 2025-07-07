package com.example.icbmpfinalboss.data.network

import com.example.icbmpfinalboss.data.models.ConvoyApiResponse
import kotlinx.coroutines.delay
import java.io.IOException

class ConvoyRepository(private val api: ConvoyApiService) {
    suspend fun fetchConvoyDataWithRetry(
        times: Int = 3,
        initialDelay: Long = 1000L,
        maxDelay: Long = 3000L,
        factor: Double = 2.0
    ): ConvoyApiResponse {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return api.getConvoyData()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        return api.getConvoyData()
    }
}
