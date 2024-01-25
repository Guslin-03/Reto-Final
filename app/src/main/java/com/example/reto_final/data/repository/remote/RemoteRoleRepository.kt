package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.Role
import com.example.reto_final.utils.Resource

interface RemoteRoleRepository {
    suspend fun getRoles() : Resource<List<Role>>
}