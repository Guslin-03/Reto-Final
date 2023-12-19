package com.example.reto_final.data.repository.remote
import com.example.reto_final.utils.MyApp

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    const val API_URI = "http://10.5.7.210/api/"

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val authToken= MyApp.userPreferences.fetchAuthToken()
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        chain.proceed(newRequest)
    } .build()

    private val retrofitClient: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(API_URI)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiInterface: APIInterface by lazy {
        retrofitClient
            .build()
            .create(APIInterface::class.java)
    }


}