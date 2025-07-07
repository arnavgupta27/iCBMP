package com.example.icbmpfinalboss.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ConvoyRetrofitProvider {
    val convoyApiService: ConvoyApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://n2hsl7lzqy62gur6euiuaolf7u0fdrtr.lambda-url.us-east-1.on.aws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConvoyApiService::class.java)
    }
}
