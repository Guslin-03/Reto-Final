package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.Group
import com.example.reto_final.utils.Resource

interface RemoteGroupRepository {

    suspend fun getGroups() : Resource<List<Group>>

    suspend fun createGroup(group: Group) : Resource<Void>

    suspend fun deleteGroup(idGroup:Int) : Resource<Void>

    suspend fun canEnterUserChat(idGroup:Int) : Resource<Int>

    suspend fun countByAndAdminId(idGroup:Int) : Resource<Int>

    suspend fun existsByIdAndUsers_Id(idGroup:Int) : Resource<Int>

    suspend fun addUserToChat(idGroup: Int, idUser: Int) : Resource<Int>

    suspend fun joinGroup(idGroup: Int) : Resource<Int>

    suspend fun leaveChat(idGroup: Int) : Resource<Int>

}