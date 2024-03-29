package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.group.PendingGroupRequest
import com.example.reto_final.data.model.userGroup.UserChatInfo

class RemoteGroupDataSource : BaseDataSource(), RemoteGroupRepository{
    override suspend fun getGroups(group: Int?)= getResult {
        RetrofitClient.apiInterface.getGroups(group)
    }

    override suspend fun createGroup(group: Group) = getResult {
       RetrofitClient.apiInterface.createGroup(group)
    }

    override suspend fun softDeleteGroup(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.softDeleteGroup(idGroup)
    }

    override suspend fun canEnterUserChat(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.canEnterUserChat(idGroup)
    }

    override suspend fun countByAndAdminId(idGroup: Int)= getResult {
        RetrofitClient.apiInterface.countByIdAndAdminId(idGroup)
    }

    override suspend fun existsByIdAndUsersId(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.existsByIdAndUsersId(idGroup)
    }

    override suspend fun addUserToChat(idGroup: Int, idUser: Int) = getResult {
        RetrofitClient.apiInterface.addUserToChat(idGroup, idUser)
    }

    override suspend fun joinToChat(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.joinToChat(idGroup)
    }

    override suspend fun leaveChat(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.leaveChat(idGroup)
    }

    override suspend fun chatThrowOut(idGroup: Int, idUser: Int) = getResult {
        RetrofitClient.apiInterface.chatThrowOut(idGroup, idUser)
    }

    override suspend fun setPendingGroups(listPendingUserChatInfo: List<UserChatInfo?>) = getResult {
        RetrofitClient.apiInterface.setPendingGroups(listPendingUserChatInfo)
    }

}