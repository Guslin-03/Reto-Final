package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.AuthRequest
import com.example.reto_final.data.ChangePasswordRequest
import com.example.reto_final.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface APIInterface {

    @POST("login")
    suspend fun login(@Body authRequest: AuthRequest): Response<User>

    @POST("logout")
    suspend fun logout(): Response<Void>

    @POST("changePassword")
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Response<Void>

}