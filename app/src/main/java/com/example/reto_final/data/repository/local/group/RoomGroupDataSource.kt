package com.example.reto_final.data.repository.local.group

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.reto_final.data.Group
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

    override suspend fun removeGroup(group: Group): Resource<Void> {
        groupDao.deleteGroup(group.toDbGroup())
        return Resource.success()
    }

}

fun DbGroup.toGroup() = Group(id, name, groupType)
fun Group.toDbGroup() = DbGroup(id, name, groupType)

//    @Transaction
//    @Query("SELECT * FROM groups ORDER BY id")
//    fun getGroupsWithMessages(): List<GroupWithMessages>

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups order by id")
    suspend fun getGroups(): List<DbGroup>
    @Insert
    suspend fun createGroup(group: DbGroup) : Long

    @Delete
    suspend fun deleteGroup(group: DbGroup)

}