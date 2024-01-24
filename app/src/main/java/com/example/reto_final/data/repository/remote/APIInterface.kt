package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.AuthRequest
import com.example.reto_final.data.model.ChangePasswordRequest
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.LoginUser
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.User
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
    suspend fun createGroup(@Body group: Group): Response<Void>
    @DELETE("chats/{id}")
    suspend fun deleteGroup(@Path("id") id: Int): Response<Void>
    @GET("chats/entryPermission/{idChat}")
    suspend fun canEnterUserChat(@Path("idChat") idGroup: Int) : Response<Int>
    @GET("chats/deletePermission/{idChat}")
    suspend fun countByIdAndAdminId(@Path("idChat") idGroup: Int): Response<Int>
    @GET("chats/existsOnChat/{idChat}")
    suspend fun existsByIdAndUsers_Id(@Path("idChat") idGroup: Int): Response<Int>
    @POST("chats/addToGroup/{idChat}")
    suspend fun addUserToChat(@Path("idChat") idGroup: Int): Response<Int>
    @DELETE("chats/leaveChat/{idChat}")
    suspend fun leaveChat(@Path("idChat") idGroup: Int): Response<Int>
    @GET("messages")
    suspend fun getMessages(): Response<List<Message>>
    @GET("messages/chat/{chatId}")
    suspend fun getMessageByChatId(@Path("chatId") idChat:Int) : Response<List<Message>>
    @POST("messages")
    suspend fun createMessage(@Body message: Message): Response<Message>
    @GET("users/chat/{chatId}")
    suspend fun getUserByChatId(@Path("chatId") idChat:Int): Response<List<User>>

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>


}