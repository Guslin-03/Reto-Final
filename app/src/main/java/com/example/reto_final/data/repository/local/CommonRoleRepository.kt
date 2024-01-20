package com.example.reto_final.data.repository.local

import com.example.reto_final.data.model.Role
import com.example.reto_final.utils.Resource

interface CommonRoleRepository {
    suspend fun createRole(role: Role): Resource<Role>
}