package com.example.reto_final.data.repository

import com.example.reto_final.data.AuthRequest
import com.example.reto_final.data.ChangePasswordRequest
import com.example.reto_final.data.repository.remote.BaseDataSource
import com.example.reto_final.data.repository.remote.RetrofitClient
import com.example.reto_final.utils.Resource

class RemoteUserDataSource: BaseDataSource(), CommonUserRepository {

    override suspend fun login(authRequest: AuthRequest) = getResult{
        RetrofitClient.apiInterface.login(authRequest)
    }

    override suspend fun logout() = getResult {
        RetrofitClient.apiInterface.logout()
    }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) = getResult {
        RetrofitClient.apiInterface.changePassword(changePasswordRequest)
    }

}