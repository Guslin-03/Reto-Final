package com.example.reto_final.data.repository.local

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.Role
import com.example.reto_final.data.model.User
import com.example.reto_final.data.model.UserRequest
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.repository.local.group.DbUserGroup
import com.example.reto_final.data.repository.local.group.toDbGroup
import com.example.reto_final.data.repository.local.message.toDbMessage
import com.example.reto_final.data.repository.local.role.toDbRole
import com.example.reto_final.data.repository.local.user.toDbUser
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteMessageDataSource
import com.example.reto_final.data.repository.remote.RemoteRoleDataSource
import com.example.reto_final.data.repository.remote.RemoteUserDataSource
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAppRoomDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

    private val remoteGroupRepository = RemoteGroupDataSource()

    private val remoteMessageRepository = RemoteMessageDataSource()

    private val remoteUserRepository = RemoteUserDataSource()

    private val remoteRoleRepository = RemoteRoleDataSource()

    private val _allMessage = MutableLiveData<Resource<List<Message>>>()

    private val _allGroup = MutableLiveData<Resource<List<Group>>>()

    private val _allUser = MutableLiveData<Resource<List<UserRequest>>>()

    private val _allRole = MutableLiveData<Resource<List<Role>>>()

    private val userGroupList = mutableListOf<Pair<Int, Int>>()

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            getAllData()
            if (_allMessage.value?.status == Resource.Status.SUCCESS
                && _allUser.value?.status == Resource.Status.SUCCESS
                && _allGroup.value?.status == Resource.Status.SUCCESS) {
                setAllData()
            }
        }

    }

    private suspend fun getAllData() {
//        _allRole.value = getAllRoles()
        _allUser.value = getAllUsers()
        _allGroup.value = getAllGroups()
        _allMessage.value = getAllMessages()
    }

    private suspend fun setAllData() {
        setAllRoles()
        setAllUsers()
        setAllGroups()
        setAllUsersToGroups()
        setAllMessages()
    }

    private suspend fun setAllRoles() {
        return withContext(Dispatchers.IO) {
//            val allRole = _allRole.value?.data
//            if (allRole != null) {
            val roles = listOf(
                Role(2, "PROFESOR"),
                Role(3, "ALUMNO")
            )
            Log.d("P1", "Hola")
            for (role in roles) {
                MyApp.db.roleDao().createRole(role.toDbRole())
            }
//            }
        }
    }

    private suspend fun setAllUsers() {
        return withContext(Dispatchers.IO) {
            val allUser = _allUser.value?.data
            if (allUser != null) {
                for (userRequest in allUser) {
                    val user = User(userRequest.id, userRequest.name, userRequest.surname, userRequest.email, userRequest.phoneNumber, userRequest.roleId)
                    MyApp.db.userDao().createUser(user.toDbUser())
                    for (int in userRequest.chatId) {
                        if (user.id != null) {
                            userGroupList.add(Pair(int, user.id!!))
                        }
                    }
                }
            }
        }
    }

    private suspend fun setAllGroups() {
        withContext(Dispatchers.IO) {
            val allGroup = _allGroup.value?.data
            if (allGroup != null) {
                for (group in allGroup) {
                    MyApp.db.groupDao().createGroup(group.toDbGroup())
                }
            }
        }
    }

    private suspend fun setAllUsersToGroups() {
        return withContext(Dispatchers.IO) {
            for (userGroup in userGroupList) {
                MyApp.db.groupDao().addUserToGroup(DbUserGroup(userGroup.first, userGroup.second))
            }
        }
    }


    private suspend fun setAllMessages() {
        return withContext(Dispatchers.IO) {
            val allMessage = _allMessage.value?.data
            if (allMessage != null) {
                for (message in allMessage) {
                    MyApp.db.messageDao().createMessage(message.toDbMessage())
                }
            }
        }
    }

    private suspend fun getAllMessages(): Resource<List<Message>> {
        return withContext(Dispatchers.IO) {
            remoteMessageRepository.getMessages()
        }
    }

    private suspend fun getAllUsers(): Resource<List<UserRequest>> {
        return withContext(Dispatchers.IO) {
            remoteUserRepository.findUsers()
        }
    }

    private suspend fun getAllGroups(): Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.getGroups()
        }
    }

    private suspend fun getAllRoles(): Resource<List<Role>> {
        return withContext(Dispatchers.IO) {
            remoteRoleRepository.getRoles()
        }
    }

}