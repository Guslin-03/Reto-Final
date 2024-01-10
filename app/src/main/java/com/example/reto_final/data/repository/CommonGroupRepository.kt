package com.example.reto_final.data.repository

import com.example.reto_final.data.Group
import com.example.reto_final.data.repository.local.group.DbGroup
import com.example.reto_final.utils.Resource

interface CommonGroupRepository {

    suspend fun getGroups() : Resource<List<Group>>

    suspend fun createGroup(group: Group) : Resource<Group>

    suspend fun removeGroup(group: Group) : Resource<Void>

}