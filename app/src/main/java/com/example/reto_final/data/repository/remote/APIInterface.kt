package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.AuthRequest
import com.example.reto_final.data.model.ChangePasswordRequest
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.LoginUser
import com.example.reto_final.data.model.Message
import com.example.reto_final.data.repository.ProfileRequest
import com.example.reto_final.data.repository.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIInterface {

    @POST("login")
    suspend fun login(@Body authRequest: AuthRequest): Response<LoginUser>

    @POST("logout")
    suspend fun logout(): Response<Void>

    @POST("changePassword")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Response<Void>

    @POST("confirmRegister")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<Void>

    @POST("updateProfile")
    suspend fun updateProfile(@Body profileRequest: ProfileRequest): Response<Void>

    /*API HIBERNATE */
    @POST("auth/login")
    suspend fun loginHibernate(@Body authRequest: AuthRequest): Response<LoginUser>
    @GET("chats")
    suspend fun getGroups(): Response<List<Group>>

    @POST("chats")
    suspend fun createGroup(@Body group: Group): Response<Group>

    @DELETE("chats/{id}")
    suspend fun deleteGroup(@Path("id") id: Int): Response<Void>

    @GET("messages")
    suspend fun getMessages(): Response<List<Message>>

    @POST("messages")
    suspend fun createMessage(@Body message: Message): Response<Message>
}