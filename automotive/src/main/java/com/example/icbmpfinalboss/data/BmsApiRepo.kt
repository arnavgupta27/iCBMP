

package com.example.icbmpfinalboss.data

import com.example.icbmpfinalboss.data.models.BmsData
import com.example.icbmpfinalboss.data.network.BmsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BmsApiRepo {
    private val apiService = BmsApi.retrofitService

    suspend fun getBmsData(): Result<List<BmsData>> {
        return withContext(Dispatchers.IO) { // Always perform network calls on a background thread.
            try {
                val data = apiService.getBmsData()
                Result.success(data)
            } catch (e: Exception) {
                // If the network call fails, wrap the exception in a failure Result.
                Result.failure(e)
            }
        }
    }
}
