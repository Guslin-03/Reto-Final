package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.Group
import com.example.reto_final.utils.Resource

class RemoteGroupDataSource : BaseDataSource(), RemoteGroupRepository{
    override suspend fun getGroups()= getResult {
        RetrofitClient.apiInterface.getGroups()
    }

    override suspend fun createGroup(group: Group) = getResult {
       RetrofitClient.apiInterface.createGroup(group)
    }

    override suspend fun deleteGroup(idGroup: Int) = getResult {

        RetrofitClient.apiInterface.deleteGroup(idGroup)
    }

    override suspend fun userHasPermission(idGroup: Int?, idUser: Int): Resource<Int> {
        TODO("Not yet implemented")
    }
}