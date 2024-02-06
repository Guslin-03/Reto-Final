package com.example.reto_final.data.repository.local.user

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.reto_final.data.model.user.User
import com.example.reto_final.data.repository.local.CommonUserRepository
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class RoomUserDataSource: CommonUserRepository {

    private val userDao: UserDao = MyApp.db.userDao()

    override suspend fun getUsers(): Resource<List<User>> {
        val response = userDao.getUsers().map { it.toUser() }
        return Resource.success(response)
    }

    override suspend fun getUsersFromGroup(idGroup: Int?): Resource<List<User>> {
        val response = userDao.getUsersFromGroup(idGroup).map { it.toUser() }
        Log.d("p1", "$response")
        return Resource.success(response)
    }

    override suspend fun createUser(user: User): Resource<User> {
        val dbUser = userDao.createUser(user.toDbUser())
        user.id = dbUser.toInt()
        return Resource.success(user)
    }

    override suspend fun deleteUserFromGroup(idUser: Int, idGroup: Int): Resource<Void> {
        val idEliminado = userDao.deleteUserForGroup(idUser, idGroup)
        return Resource.success()
    }

    override suspend fun userIsAdmin(idUser: Int, idGroup: Int): Resource<Int> {
        val isAdmin = userDao.userIsAdmin(idUser, idGroup)
        return Resource.success(isAdmin)
    }

    override suspend fun getLastUser(): Resource<User?> {
        val response = userDao.getLastUser()?.toUser()
        return Resource.success(response)
    }


}

fun DbUser.toUser() = User(id, name, surname, email, phoneNumber, roleId)
fun User.toDbUser() = DbUser(id, name, surname, email, phone_number1, roleId)

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name")
    suspend fun getUsers() : List<DbUser>
    @Query("SELECT users.id, users.name, users.surname, users.email, users.phoneNumber, users.roleId FROM users \n" +
            "JOIN group_user ON users.id == group_user.userId\n" +
            "WHERE group_user.groupId == :idGroup AND group_user.deleted IS NULL")
    suspend fun getUsersFromGroup(idGroup: Int?): List<DbUser>
    @Insert
    suspend fun createUser(dbUser: DbUser) : Long
    @Query("DELETE FROM group_user WHERE groupId = :groupId AND  userId = :userId")
    suspend fun deleteUserForGroup(userId: Int, groupId: Int) : Int
    @Query("SELECT COUNT(id) FROM groups WHERE id = :idGroup AND adminId = :idAdmin")
    suspend fun userIsAdmin(idAdmin: Int, idGroup: Int) : Int
    @Query("SELECT * FROM users WHERE id = (SELECT MAX(id) FROM users)")
    suspend fun getLastUser(): DbUser?

}

