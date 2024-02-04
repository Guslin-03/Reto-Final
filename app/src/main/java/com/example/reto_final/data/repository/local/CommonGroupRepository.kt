package com.example.reto_final.data.repository.local

import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.userGroup.UserChatInfo
import com.example.reto_final.utils.Resource

interface CommonGroupRepository {

    suspend fun getGroups() : Resource<List<Group>>
    suspend fun createGroupAsAdmin(group: Group): Resource<Void>
    suspend fun createGroup(group: Group) : Resource<Group>
    suspend fun softDeleteGroup(group: Group) : Resource<Void>
    suspend fun userHasPermission(idGroup: Int?, idUser: Int): Resource<Int>
    suspend fun userHasPermissionToDelete(idGroup: Int?, idUser: Int): Resource<Int>
    suspend fun addUserToGroup(userChatInfo: UserChatInfo): Resource<Int>
    suspend fun leaveGroup(idGroup: Int, idUser: Int): Resource<Int>
    suspend fun userHasAlreadyInGroup(idGroup: Int?, idUser: Int): Resource<Int>
    suspend fun getLastGroup(): Resource<Group?>

}