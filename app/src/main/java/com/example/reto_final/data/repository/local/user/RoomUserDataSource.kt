package com.example.reto_final.data.repository.local.user

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.reto_final.data.model.User
import com.example.reto_final.data.repository.CommonUserRepository
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class RoomUserDataSource: CommonUserRepository {

    private val userDao: UserDao = MyApp.db.userDao()

    override suspend fun getUsersFromGroup(idGroup: Int?): Resource<List<User>> {
        val response = userDao.getUsersFromGroup(idGroup).map { it.toUser() }
        return Resource.success(response)
    }

    override suspend fun createUser(user: User): Resource<User> {
        val dbUser = userDao.createUser(user.toDbUser())
        user.id = dbUser.toInt()
        return Resource.success(user)
    }

    override suspend fun deleteUser(user: User): Resource<Void> {
        val idEliminado = userDao.deleteUser(user.toDbUser())
        return Resource.success()
    }

}

fun DbUser.toUser() = User(id, name, surname, email, phoneNumber)
fun User.toDbUser() = DbUser(id, name, surname, email, phoneNumber)

@Dao
interface UserDao {
    @Query("SELECT users.id, users.name, users.surname, users.email, users.phoneNumber FROM users \n" +
            "JOIN group_user ON users.id == group_user.userId\n" +
            "WHERE group_user.groupId == :idGroup")
    suspend fun getUsersFromGroup(idGroup: Int?): List<DbUser>
    @Insert
    suspend fun createUser(dbUser: DbUser) : Long
    @Delete
    suspend fun deleteUser(dbUser: DbUser) : Int

}
