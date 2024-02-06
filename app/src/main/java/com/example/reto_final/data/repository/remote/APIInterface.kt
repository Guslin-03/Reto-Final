package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.user.AuthRequest
import com.example.reto_final.data.model.user.ChangePasswordRequest
import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.user.LoginUser
import com.example.reto_final.data.model.Role
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.user.User
import com.example.reto_final.data.model.user.UserRequest
import com.example.reto_final.data.model.message.MessageResponse
import com.example.reto_final.data.model.ProfileRequest
import com.example.reto_final.data.model.RegisterRequest
import com.example.reto_final.data.model.group.GroupResponse
import com.example.reto_final.data.model.group.PendingGroupRequest
import com.example.reto_final.data.model.message.PendingMessageRequest
import com.example.reto_final.data.model.userGroup.UserChatInfo
import com.example.reto_final.data.socket.SocketMessageReq
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIInterface {

    @POST("login")
    suspend fun login(@Body authRequest: AuthRequest) : Response<LoginUser>

    @POST("logout")
    suspend fun logout() : Response<Void>

    @POST("changePassword")
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequest) : Response<Void>

    @POST("confirmRegister")
    suspend fun register(@Body registerRequest: RegisterRequest) : Response<Void>

    @POST("updateProfile")
    suspend fun updateProfile(@Body profileRequest: ProfileRequest) : Response<Void>

    /*API HIBERNATE */
    @POST("auth/login")
    suspend fun loginHibernate(@Body authRequest: AuthRequest) : Response<LoginUser>
    @GET("chats/findAll/{id}")
    suspend fun getGroups(@Path("id") groupId: Int?) : Response<List<Group>>
    @POST("chats")
    suspend fun createGroup(@Body group: Group) : Response<Group>
    @DELETE("chats/{id}")
    suspend fun softDeleteGroup(@Path("id") id: Int) : Response<Group>
    @GET("chats/entryPermission/{idChat}")
    suspend fun canEnterUserChat(@Path("idChat") idGroup: Int) : Response<Int>
    @GET("chats/deletePermission/{idChat}")
    suspend fun countByIdAndAdminId(@Path("idChat") idGroup: Int) : Response<Int>
    @GET("chats/existsOnChat/{idChat}")
    suspend fun existsByIdAndUsersId(@Path("idChat") idGroup: Int) : Response<Int>
    @POST("chats/addUserToChat/{idChat}/{idUser}")
    suspend fun addUserToChat(@Path("idChat") idGroup: Int, @Path("idUser") idUser: Int) : Response<UserChatInfo>
    @POST("chats/joinToChat/{idChat}")
    suspend fun joinToChat(@Path("idChat") idGroup: Int) : Response<UserChatInfo>
    @DELETE("chats/leaveChat/{idChat}")
    suspend fun leaveChat(@Path("idChat") idGroup: Int) : Response<UserChatInfo>
    @DELETE("chats/throwFromChat/{idChat}/{idUser}")
    suspend fun chatThrowOut(@Path("idChat")idGroup: Int, @Path("idUser")idUser: Int) : Response<UserChatInfo>
    @GET("messages/findAll/{id}")
    suspend fun getMessages(@Path("id") messageId: Int?) : Response<List<MessageResponse>>
    @GET("messages/chat/{chatId}")
    suspend fun getMessageByChatId(@Path("chatId") idChat:Int) : Response<List<Message>>
    @POST("messages")
    suspend fun createMessage(@Body message: Message) : Response<Message>
    @GET("users/chat/{chatId}")
    suspend fun getUserByChatId(@Path("chatId") idChat:Int) : Response<List<User>>
    @GET("users/find/{email}")
    suspend fun getUserByEmail(@Path("email") email:String) : Response<Int>
    @POST("users/reset/{email}")
    suspend fun resetPassword(@Path("email") email:String) : Response<Int>
    @GET("users/findAll/{id}")
    suspend fun findUsers(@Path("id") userId: Int?) : Response<List<UserRequest>>
    @GET("roles")
    suspend fun getRoles() : Response<List<Role>>
    @POST("pendingMessages")
    suspend fun setPendingMessages(@Body listPendingMessage: List<PendingMessageRequest?>) : Response<List<MessageResponse>>
    @POST("pendingGroups")
    suspend fun setPendingGroups(@Body listPendingGroup: List<PendingGroupRequest?>) : Response<List<GroupResponse>>

}