package com.example.reto_final.data.repository.local.role

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import com.example.reto_final.data.model.Role
import com.example.reto_final.data.repository.local.CommonRoleRepository
import com.example.reto_final.data.repository.local.user.UserRoleType
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class RoomRoleDataSource : CommonRoleRepository {

    private val roleDao: RoleDao = MyApp.db.roleDao()

    override suspend fun createRole(role: Role): Resource<Role> {

        try {
            val dbRole = roleDao.createRole(role.toDbRole())
            role.id = dbRole.toInt()

        } catch (ex : Exception) {
            Log.d("Prueba", "Prueba")
        }
        return Resource.success(role)

    }

}

fun DbRole.toRole() = Role(id, type)
fun Role.toDbRole() = DbRole(id, name)

@Dao
interface RoleDao {
    @Insert
    suspend fun createRole(role: DbRole) : Long
}