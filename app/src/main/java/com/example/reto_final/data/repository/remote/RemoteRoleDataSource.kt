package com.example.reto_final.data.repository.remote


class RemoteRoleDataSource : BaseDataSource(), RemoteRoleRepository {

    override suspend fun getRoles()= getResult {
        RetrofitClient.apiInterface.getRoles()
    }
}