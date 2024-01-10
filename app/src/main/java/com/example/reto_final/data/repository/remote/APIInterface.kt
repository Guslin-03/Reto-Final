package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.AuthRequest
import com.example.reto_final.data.model.ChangePasswordRequest
import com.example.reto_final.data.model.User
import com.example.reto_final.data.repository.ProfileRequest
import com.example.reto_final.data.repository.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIInterface {

    @POST("login")
    suspend fun login(@Body authRequest: AuthRequest): Response<User>

    @POST("logout")
    suspend fun logout(): Response<Void>

    @POST("changePassword")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Response<Void>

    @POST("confirmRegister")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<Void>

    @POST("updateProfile")
    suspend fun updateProfile(@Body profileRequest: ProfileRequest): Response<Void>

}