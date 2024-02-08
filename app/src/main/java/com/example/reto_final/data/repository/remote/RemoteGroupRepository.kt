package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.group.GroupResponse
import com.example.reto_final.data.model.group.PendingGroupRequest
import com.example.reto_final.data.model.user.User
import com.example.reto_final.data.model.userGroup.UserChatInfo
import com.example.reto_final.utils.Resource

interface RemoteGroupRepository {

    suspend fun getGroups(group: Int?) : Resource<List<Group>>

    suspend fun createGroup(group: Group) : Resource<Group>

    suspend fun softDeleteGroup(idGroup:Int) : Resource<Group>

    suspend fun canEnterUserChat(idGroup:Int) : Resource<Int>

    suspend fun countByAndAdminId(idGroup:Int) : Resource<Int>

    suspend fun existsByIdAndUsersId(idGroup:Int) : Resource<Int>

    suspend fun addUserToChat(idGroup: Int, idUser: Int) : Resource<UserChatInfo>

    suspend fun joinToChat(idGroup: Int) : Resource<UserChatInfo>

    suspend fun leaveChat(idGroup: Int) : Resource<UserChatInfo>

    suspend fun chatThrowOut(idGroup: Int, idUser: Int) : Resource<UserChatInfo>
    suspend fun setPendingGroups(listUserChatInfoRequest : List<UserChatInfo?>) : Resource<List<UserChatInfo>>

}