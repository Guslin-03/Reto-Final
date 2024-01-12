package com.example.reto_final.data.repository

import com.example.reto_final.data.model.AuthRequest
import com.example.reto_final.data.model.ChangePasswordRequest
import com.example.reto_final.data.repository.remote.BaseDataSource
import com.example.reto_final.data.repository.remote.RetrofitClient

class RemoteLoginUserDataSource: BaseDataSource(), CommonLoginUserRepository {

    override suspend fun login(authRequest: AuthRequest) = getResult{
        RetrofitClient.apiInterface.login(authRequest)
    }

    override suspend fun logout() = getResult {
        RetrofitClient.apiInterface.logout()
    }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) = getResult {
        RetrofitClient.apiInterface.changePassword(changePasswordRequest)
    }

    override suspend fun register(registerRequest: RegisterRequest) = getResult {
        RetrofitClient.apiInterface.register(registerRequest)
    }

    override suspend fun updateProfile(profileRequest: ProfileRequest) = getResult {
        RetrofitClient.apiInterface.updateProfile(profileRequest)
    }

}