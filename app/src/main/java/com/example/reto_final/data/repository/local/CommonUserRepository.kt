package com.example.reto_final.data.repository.local

import com.example.reto_final.data.model.User
import com.example.reto_final.utils.Resource

interface CommonUserRepository {
    suspend fun getUsersFromGroup(idGroup: Int?) : Resource<List<User>>
    suspend fun createUser(user: User) : Resource<User>
    suspend fun deleteUserFromGroup(idUser: Int, idGroup: Int): Resource<Void>
    suspend fun userIsAdmin(idUser: Int, idGroup: Int) : Resource<Int>
}