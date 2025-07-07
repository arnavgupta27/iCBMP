// File: app/src/main/java/com/example/icbmpfinalboss/data/network/BmsApiService.kt

package com.example.icbmpfinalboss.data.network

import com.example.icbmpfinalboss.data.models.BmsData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface BmsApiService {
    @GET(".") // The new URL points directly to the data, so we use "." for the endpoint.
    suspend fun getBmsData(): List<BmsData>
}

// Creates and configures a single, reusable Retrofit instance.
object BmsApi {
    // The new base URL from your AWS Lambda function.
    private const val BASE_URL = "https://eoil6qngvr7ptbxkbujirlqhfm0avbsk.lambda-url.us-east-1.on.aws/"

    // A simple OkHttp client with a logger to see network activity in Logcat.
    // Notice: No API key interceptor is needed for this endpoint.
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    // The lazy-initialized Retrofit service.
    val retrofitService: BmsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BmsApiService::class.java)
    }
}


val convoyApiService: ConvoyApiService by lazy {
    Retrofit.Builder()
        .baseUrl("https://n2hsl7lzqy62gur6euiuaolf7u0fdrtr.lambda-url.us-east-1.on.aws/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ConvoyApiService::class.java)
}

