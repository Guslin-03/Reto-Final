package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.Group
import com.example.reto_final.utils.Resource

interface RemoteGroupRepository {

    suspend fun getGroups(group: Int?) : Resource<List<Group>>

    suspend fun createGroup(group: Group) : Resource<Group>

    suspend fun softDeleteGroup(idGroup:Int) : Resource<Void>

    suspend fun canEnterUserChat(idGroup:Int) : Resource<Int>

    suspend fun countByAndAdminId(idGroup:Int) : Resource<Int>

    suspend fun existsByIdAndUsersId(idGroup:Int) : Resource<Int>

    suspend fun addUserToChat(idGroup: Int, idUser: Int) : Resource<Int>

    suspend fun joinGroup(idGroup: Int) : Resource<Int>

    suspend fun leaveChat(idGroup: Int) : Resource<Int>

    suspend fun chatThrowOut(idGroup: Int, idUser: Int) : Resource<Int>

}