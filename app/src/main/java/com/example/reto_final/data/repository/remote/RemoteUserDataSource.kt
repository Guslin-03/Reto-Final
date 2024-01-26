package com.example.reto_final.data.repository.remote

class RemoteUserDataSource : BaseDataSource(), RemoteUserRepository{

    override suspend fun getUserByChatId(idChat:Int) = getResult {
        RetrofitClient.apiInterface.getUserByChatId(idChat)
    }

    override suspend fun findUsers() = getResult {
        RetrofitClient.apiInterface.findUsers()
    }

}