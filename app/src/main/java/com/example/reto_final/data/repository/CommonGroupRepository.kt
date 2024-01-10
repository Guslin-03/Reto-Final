package com.example.reto_final.data.repository

import com.example.reto_final.data.model.Group
import com.example.reto_final.utils.Resource

interface CommonGroupRepository {

    suspend fun getGroups() : Resource<List<Group>>

    suspend fun createGroup(group: Group) : Resource<Group>

    suspend fun deleteGroup(group: Group) : Resource<Void>

}