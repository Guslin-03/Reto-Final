package com.example.reto_final.data.repository.local.group

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.userGroup.UserChatInfo
import com.example.reto_final.data.repository.local.CommonGroupRepository
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import java.util.Date

class RoomGroupDataSource : CommonGroupRepository {

    private val groupDao: GroupDao = MyApp.db.groupDao()

    override suspend fun getGroups(userId: Int): Resource<List<Group>> {
        val response = groupDao.getGroups(userId).map { it.toGroup() }
        return Resource.success(response)
    }

    override suspend fun createGroupAsAdmin(group: Group): Resource<Void> {
        return try {
            groupDao.createGroup(group.toDbGroup())
            val user = MyApp.userPreferences.getUser()
            val currentDate = System.currentTimeMillis()

            Log.d("p1", "Entra")
            if (user != null) group.id?.let { DbUserGroup(group.id!!, user.id, Date(currentDate), null) }
                ?.let { groupDao.addUserToGroup(it) }
            Resource.success()
        } catch (exception: SQLiteConstraintException) {
            Resource.error("El nombre del grupo ya esta en uso")
        }

    }

    //NUNCA SE CREA CON EL ADMIN
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
        val response = groupDao.softDeleteGroup(group.id, group.deleted)
        return if (response == 1) {
            groupDao.softDeleteRelations(group.id, group.deleted)
            Resource.success()
        } else {
            Resource.error("Se ha producido un error al eliminar el grupo")
        }

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
    @Transaction
    override suspend fun addUserToGroup(userChatInfo: UserChatInfo): Resource<Int> {
        val dbUserChatInfo = DbUserGroup(userChatInfo.chatId,
            userChatInfo.userId,
            Date(userChatInfo.joined),
            userChatInfo.deleted?.let { Date(it) })
        //logica de solo crear el nuevo en la base de datos si es la primera carga o el usuario aun no existia en la n:m
        //Significa que ya existia esa relacion asi que solo tenemos que quitar el deleted
        if(groupDao.getCountByUserIdAndChatId(userChatInfo.chatId, userChatInfo.userId) > 0 ){
            try {
                val affectedRowCount = groupDao.updateJoinDateInGroupUser(userChatInfo.chatId, userChatInfo.userId, userChatInfo.joined)
                return Resource.success(affectedRowCount)
            } catch (exception: Exception) {
                return Resource.error("Failed to update join date in group user")
            }
        }else{
            val response = groupDao.addUserToGroup(DbUserGroup(userChatInfo.chatId, userChatInfo.userId, Date(userChatInfo.joined), null))
            return Resource.success(response.toInt())
        }
    }

    override suspend fun leaveGroup(idGroup: Int, idUser: Int): Resource<Int> {
        return try {
            val affectedRowCount = groupDao.updateDeleteDateInGroup(idGroup, idUser, Date().time)
            Resource.success(affectedRowCount)
        } catch (exception: Exception) {
            Resource.error("Failed leave a group")
        }
    }

    override suspend fun chatThrowOutLocal(userChatInfo: UserChatInfo): Resource<Int> {
        return try {
            val affectedRowCount = groupDao.updateDeleteDateInGroup(userChatInfo.chatId, userChatInfo.userId, userChatInfo.deleted)
            Resource.success(affectedRowCount)
        } catch (exception: Exception) {
            Resource.error("Failed to throw out a user from group")
        }
    }

    override suspend fun getLastGroup(): Resource<Group?> {
        val response = groupDao.getLastGroup()
        return Resource.success(response)
    }

    override suspend fun getPendingGroups(): Resource<List<Group>> {
        val pendingGroups = groupDao.getPendingGroups().map { it.toGroup() }
        return Resource.success(pendingGroups)
    }

    //TESTEAR
    override suspend fun updateGroup(group: Group): Resource<Group> {
        val dbGroup = groupDao.updateGroup(group.id ,group.deleted)
        group.id = dbGroup
        return Resource.success(group)
    }

}

fun DbGroup.toGroup() = Group(id, name, chatEnumType, created?.time, deleted?.time, localDeleted?.time, adminId)
fun Group.toDbGroup() = DbGroup(id, name, type, created?.let { Date(it) }, deleted?.let { Date(it) }, localDeleted?.let { Date(it) }, adminId)

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups WHERE (type = 'PUBLIC' AND deleted IS NULL) OR (type = 'PRIVATE' AND id IN (SELECT groupId FROM group_user WHERE userId = :userId) AND deleted IS NULL) ORDER BY id")
    suspend fun getGroups(userId:Int): List<DbGroup>
    @Insert
    suspend fun createGroup(group: DbGroup) : Long
    @Query("UPDATE groups SET deleted = :deleted WHERE id = :idGroup")
    suspend fun softDeleteGroup(idGroup: Int?, deleted: Long?) : Int
    @Query("UPDATE group_user SET deleted = :deleted WHERE groupId = :groupId")
    suspend fun softDeleteRelations(groupId: Int?, deleted: Long?) : Int
    @Query("SELECT COUNT(groupId) FROM group_user WHERE groupId = :idGroup AND userId = :idUser AND deleted IS NULL")
    suspend fun userHasPermission(idGroup: Int?, idUser: Int): Int
    @Query("SELECT COUNT(id) FROM groups WHERE id = :idGroup AND adminId = :idUser")
    suspend fun userHasPermissionToDelete(idGroup: Int?, idUser: Int): Int
    @Insert
    suspend fun addUserToGroup(userGroup: DbUserGroup) : Long
    @Query("SELECT COUNT(groupId) FROM group_user WHERE groupId = :idGroup AND userId = :idUser")
    suspend fun userHasAlreadyInGroup(idGroup: Int?, idUser: Int): Int
    @Query("SELECT COUNT(userId) FROM group_user WHERE groupId = :idGroup AND userId = :idUser")
    suspend fun getCountByUserIdAndChatId(idGroup: Int, idUser: Int): Int
    @Query("SELECT * FROM groups WHERE id = (SELECT MAX(id) FROM groups)")
    suspend fun getLastGroup(): Group?
    @Query("SELECT * FROM groups WHERE id = (SELECT MAX(id) FROM groups)")
    suspend fun getPendingGroups(): List<DbGroup>
    @Query("UPDATE group_user SET joined = :joined, deleted = null WHERE groupId = :idGroup AND userId = :idUser")
    suspend fun updateJoinDateInGroupUser(idGroup: Int, idUser: Int, joined: Long): Int
    @Query("UPDATE group_user SET deleted = :deleted WHERE groupId = :idGroup AND userId = :idUser")
    suspend fun updateDeleteDateInGroup(idGroup: Int, idUser: Int, deleted: Long?): Int
    @Query("UPDATE groups SET deleted = :deleted WHERE id = :groupId")
    suspend fun updateGroup(groupId: Int?, deleted: Long?) : Int

}