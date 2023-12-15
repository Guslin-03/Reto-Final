package com.example.reto_final.data.repository

import com.example.reto_final.data.AuthRequest
import com.example.reto_final.data.repository.remote.BaseDataSource
import com.example.reto_final.data.repository.remote.RetrofitClient

class RemoteUserDataSource: BaseDataSource(), CommonUserRepository {

    override suspend fun login(authRequest: AuthRequest) = getResult{
        RetrofitClient.apiInterface.login(authRequest)
    }
    override suspend fun getUserInfo() = getResult{
        RetrofitClient.apiInterface.getUserInfo()
    }

}