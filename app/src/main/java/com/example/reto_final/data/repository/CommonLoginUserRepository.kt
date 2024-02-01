package com.example.reto_final.data.repository

import com.example.reto_final.data.model.AuthRequest
import com.example.reto_final.data.model.ChangePasswordRequest
import com.example.reto_final.data.model.LoginUser
import com.example.reto_final.data.model.ProfileRequest
import com.example.reto_final.data.model.RegisterRequest
import com.example.reto_final.utils.Resource

interface CommonLoginUserRepository {

    suspend fun login(authRequest: AuthRequest): Resource<LoginUser>

    suspend fun loginHibernate(authRequest: AuthRequest): Resource<LoginUser>

    suspend fun logout(): Resource<Void>

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Resource<Void>

    suspend fun register(registerRequest: RegisterRequest): Resource<Void>

    suspend fun updateProfile(profileRequest: ProfileRequest): Resource<Void>

    suspend fun findUserByEmail(email: String): Resource<Int>

    suspend fun resetPassword(email:String): Resource<Int>
}