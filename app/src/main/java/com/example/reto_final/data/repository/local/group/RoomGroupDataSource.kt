package com.example.reto_final.data.repository.local.group

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.local.CommonGroupRepository
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import java.util.Date

class RoomGroupDataSource : CommonGroupRepository {

    private val groupDao: GroupDao = MyApp.db.groupDao()

    override suspend fun getGroups(): Resource<List<Group>> {
        val response = groupDao.getGroups().map { it.toGroup() }
        return Resource.success(response)
    }

    override suspend fun createGroupAsAdmin(group: Group): Resource<Void> {
        return try {
            val dbGroup = groupDao.createGroup(group.toDbGroup())
            val user = MyApp.userPreferences.getUser()
            Log.d("p1", "Entra")
            if (user != null) groupDao.addUserToGroup(DbUserGroup(dbGroup.toInt(), user.id, null, null))
            Resource.success()
        } catch (exception: SQLiteConstraintException) {
            Resource.error("El nombre del grupo ya esta en uso")
        }

    }

    override suspend fun createGroup(group: Group): Resource<Group> {
        return try {
            val idGroupCreated = groupDao.createGroup(group.toDbGroup())
            group.id = idGroupCreated.toInt()
            Resource.success(group)
        } catch (exception: SQLiteConstraintException) {
            Resource.success(group)
//            Resource.error("El nombre del grupo ya esta en uso")
        }
    }

    override suspend fun softDeleteGroup(group: Group): Resource<Void> {
        Log.i("p1", "$group")
        groupDao.softDeleteGroup(group.id, group.deleted)
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
        Log.d("p1", "Entra")
        val response = groupDao.addUserToGroup(DbUserGroup(idGroup, idUser, null, null))
        return Resource.success(response.toInt())
    }

    override suspend fun leaveGroup(idGroup: Int, idUser: Int): Resource<Int> {
        groupDao.leaveGroup(idGroup, idUser)
        return Resource.success()
    }

    override suspend fun getLastGroup(): Resource<Group?> {
        val response = groupDao.getLastGroup()
        return Resource.success(response)
    }

}

fun DbGroup.toGroup() = Group(id, name, chatEnumType, created?.time, deleted?.time, adminId)
fun Group.toDbGroup() = DbGroup(id, name, type, created?.let { Date(it) }, deleted?.let { Date(it) }, adminId)

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups WHERE deleted IS NULL order by id")
    suspend fun getGroups(): List<DbGroup>
    @Insert
    suspend fun createGroup(group: DbGroup) : Long
    @Query("UPDATE groups SET deleted = :deleted WHERE id = :idGroup")
    suspend fun softDeleteGroup(idGroup: Int?, deleted: Long?) : Int
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

    @Query("SELECT * FROM groups WHERE id = (SELECT MAX(id) FROM groups)")
    suspend fun getLastGroup(): Group?

}