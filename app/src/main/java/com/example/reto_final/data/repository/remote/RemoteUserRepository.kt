package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.User
import com.example.reto_final.data.model.UserRequest
import com.example.reto_final.utils.Resource

interface RemoteUserRepository {

    suspend fun findUsers(user: Int?): Resource<List<UserRequest>>
    suspend fun getUserByChatId(idChat:Int) : Resource<List<User>>
}