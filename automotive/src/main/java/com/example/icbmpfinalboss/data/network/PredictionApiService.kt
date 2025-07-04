// File: app/src/main/java/com/example/icbmpfinalboss/data/network/PredictionApiService.kt
// (You can rename your AlertApiService.kt to this)

package com.example.icbmpfinalboss.data.network

import com.example.icbmpfinalboss.data.models.Prediction // Use the new Prediction class
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PredictionApiService {
    @GET(".")
    suspend fun getPredictions(): List<Prediction> // It now returns a List of Prediction
}

class PredictionApiRepo { // Renamed from AlertApiRepo
    private val predictionService: PredictionApiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        Retrofit.Builder()
            .baseUrl("https://ljlxmmeoybr3jsr2k45xuzodry0ngftv.lambda-url.us-east-1.on.aws/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PredictionApiService::class.java)
    }

    suspend fun fetchPredictions(): Result<List<Prediction>> {
        return try {
            Result.success(predictionService.getPredictions())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
