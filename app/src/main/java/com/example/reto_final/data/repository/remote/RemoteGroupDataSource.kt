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

    override suspend fun canEnterUserChat(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.canEnterUserChat(idGroup)
    }

    override suspend fun countByAndAdminId(idGroup: Int)= getResult {
        RetrofitClient.apiInterface.countByIdAndAdminId(idGroup)
    }

    override suspend fun existsByIdAndUsers_Id(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.existsByIdAndUsers_Id(idGroup)
    }

    override suspend fun addUserToChat(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.addUserToChat(idGroup)
    }

    override suspend fun leaveChat(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.leaveChat(idGroup)
    }

}