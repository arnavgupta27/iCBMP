package com.example.icbmpfinalboss.data.network

import com.example.icbmpfinalboss.data.models.ConvoyApiResponse
import retrofit2.http.GET

interface ConvoyApiService {
    @GET("/")
    suspend fun getConvoyData(): ConvoyApiResponse
}
