package com.example.reto_final.data.repository.local.group

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.local.CommonGroupRepository
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class RoomGroupDataSource : CommonGroupRepository {

    private val groupDao: GroupDao = MyApp.db.groupDao()

    override suspend fun getGroups(): Resource<List<Group>> {
        val response = groupDao.getGroups().map { it.toGroup() }
        return Resource.success(response)
    }

    override suspend fun createGroup(group: Group): Resource<Group> {
        val dbGroup = groupDao.createGroup(group.toDbGroup())
        group.id = dbGroup.toInt()
        return Resource.success(group)
    }

    override suspend fun deleteGroup(group: Group): Resource<Void> {
        groupDao.deleteGroup(group.toDbGroup())
        return Resource.success()
    }

    override suspend fun userHasPermission(idGroup: Int?, idUser: Int): Resource<Int> {
        val result = groupDao.userHasPermission(idGroup, idUser)
        return Resource.success(result)
    }

    override suspend fun userHasPermissionToDelete(idGroup: Int?, idUser: Int): Resource<Int> {
        val result = groupDao.userHasPermissionToDelete(idGroup, idUser)
        return Resource.success(result)
    }

    override suspend fun userHasAlreadyInGroup(idGroup: Int?, idUser: Int): Resource<Int> {
        val values = groupDao.userHasAlreadyInGroup(idGroup, idUser)
        return Resource.success(values)
    }

    override suspend fun addUserToGroup(idGroup: Int, idUser: Int): Resource<Int> {
        val response = groupDao.addUserToGroup(DbUserGroup(idGroup, idUser))
        return Resource.success(response.toInt())
    }

    override suspend fun leaveGroup(idGroup: Int, idUser: Int): Resource<Int> {
        groupDao.leaveGroup(idGroup, idUser)
        return Resource.success()
    }

}

fun DbGroup.toGroup() = Group(id, name, chatEnumType, adminId)
fun Group.toDbGroup() = DbGroup(id, name, type, adminId)

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups order by id")
    suspend fun getGroups(): List<DbGroup>
    @Insert
    suspend fun createGroup(group: DbGroup) : Long
    @Delete
    suspend fun deleteGroup(group: DbGroup) : Int
    @Query("SELECT COUNT(groupId) FROM group_user WHERE groupId = :idGroup AND userId = :idUser")
    suspend fun userHasPermission(idGroup: Int?, idUser: Int): Int
    @Query("SELECT COUNT(id) FROM groups WHERE id = :idGroup AND adminId = :idUser")
    suspend fun userHasPermissionToDelete(idGroup: Int?, idUser: Int): Int
    @Insert
    suspend fun addUserToGroup(userGroup: DbUserGroup) : Long
    @Query("DELETE FROM group_user WHERE groupId = :idGroup AND userId = :idUser")
    suspend fun leaveGroup(idGroup: Int, idUser: Int) : Int
    @Query("SELECT COUNT(groupId) FROM group_user WHERE groupId = :idGroup AND userId = :idUser")
    suspend fun userHasAlreadyInGroup(idGroup: Int?, idUser: Int): Int

}