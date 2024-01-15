package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.Group
import com.example.reto_final.utils.Resource

interface RemoteGroupRepository {

    suspend fun getGroups() : Resource<List<Group>>

    suspend fun createGroup(group: Group) : Resource<Group>

    suspend fun deleteGroup(idGroup:Int) : Resource<Void>

    suspend fun userHasPermission(idGroup: Int?, idUser: Int): Resource<Int>

}