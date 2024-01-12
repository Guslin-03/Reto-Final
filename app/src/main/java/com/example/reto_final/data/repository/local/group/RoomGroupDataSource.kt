package com.example.reto_final.data.repository.local.group

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.CommonGroupRepository
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
        val idEliminado = groupDao.deleteGroup(group.toDbGroup())
        return Resource.success()
    }

    override suspend fun userHasPermission(idGroup: Int?, idUser: Int): Resource<Int> {
        val result = groupDao.userHasPermission(idGroup, idUser)
        return Resource.success(result)

    }

}

fun DbGroup.toGroup() = Group(id, name, groupType, adminId)
fun Group.toDbGroup() = DbGroup(id, name, groupType, adminId)

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

}