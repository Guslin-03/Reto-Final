package com.example.reto_final.data.repository

import com.example.reto_final.data.AuthRequest
import com.example.reto_final.data.ChangePasswordRequest
import com.example.reto_final.data.User
import com.example.reto_final.utils.Resource

interface CommonUserRepository {

    suspend fun login(authRequest: AuthRequest): Resource<User>

    suspend fun logout(): Resource<Void>

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Resource<Void>

    suspend fun register(registerRequest: RegisterRequest): Resource<Void>

    suspend fun updateProfile(profileRequest: ProfileRequest): Resource<Void>

}