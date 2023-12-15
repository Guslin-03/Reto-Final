package com.example.reto_final.data.repository

import com.example.reto_final.data.AuthRequest
import com.example.reto_final.data.User
import com.example.reto_final.utils.Resource

interface CommonUserRepository {

    suspend fun login(authRequest: AuthRequest): Resource<User>
    suspend fun getUserInfo(): Resource<User>

}