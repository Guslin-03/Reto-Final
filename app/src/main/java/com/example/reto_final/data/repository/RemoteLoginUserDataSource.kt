package com.example.reto_final.data.repository

import com.example.reto_final.data.model.user.AuthRequest
import com.example.reto_final.data.model.user.ChangePasswordRequest
import com.example.reto_final.data.model.ProfileRequest
import com.example.reto_final.data.model.RegisterRequest
import com.example.reto_final.data.repository.remote.BaseDataSource
import com.example.reto_final.data.repository.remote.RetrofitClient
import com.example.reto_final.data.repository.remote.RetrofitClientLaravel

class RemoteLoginUserDataSource: BaseDataSource(), CommonLoginUserRepository {

    override suspend fun login(authRequest: AuthRequest) = getResult{
        RetrofitClientLaravel.apiInterface.login(authRequest)
    }

    override suspend fun loginHibernate(authRequest: AuthRequest)= getResult {
        RetrofitClient.apiInterface.loginHibernate(authRequest)
    }

    override suspend fun logout() = getResult {
        RetrofitClientLaravel.apiInterface.logout()
    }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) = getResult {
        RetrofitClientLaravel.apiInterface.changePassword(changePasswordRequest)
    }

    override suspend fun register(registerRequest: RegisterRequest) = getResult {
        RetrofitClientLaravel.apiInterface.register(registerRequest)
    }

    override suspend fun updateProfile(profileRequest: ProfileRequest) = getResult {
        RetrofitClientLaravel.apiInterface.updateProfile(profileRequest)
    }

    override suspend fun findUserByEmail(email: String) = getResult {
        RetrofitClient.apiInterface.getUserByEmail(email)
    }

    override suspend fun resetPassword(email: String) =getResult {
        RetrofitClient.apiInterface.resetPassword(email)
    }

}