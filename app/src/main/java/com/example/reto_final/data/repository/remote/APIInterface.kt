package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.AuthRequest
import com.example.reto_final.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIInterface {

    @POST("login")
    suspend fun login(@Body authRequest: AuthRequest): Response<User>

    @GET("/auth/me")
    suspend fun getUserInfo(): Response<User>

}