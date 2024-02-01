package com.example.reto_final.data.repository.local

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.Role
import com.example.reto_final.data.model.User
import com.example.reto_final.data.model.UserRequest
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.message.MessageResponse
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.data.repository.local.role.RoomRoleDataSource
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupRepository
import com.example.reto_final.data.repository.remote.RemoteMessageRepository
import com.example.reto_final.data.repository.remote.RemoteRoleRepository
import com.example.reto_final.data.repository.remote.RemoteUserRepository
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PopulateLocalDataBase(
    private val groupLocalRepository: RoomGroupDataSource,
    private val remoteGroupRepository: RemoteGroupRepository,

    private val messageLocalRepository: RoomMessageDataSource,
    private val remoteMessageRepository: RemoteMessageRepository,

    private val userLocalRepository: RoomUserDataSource,
    private val remoteUserRepository: RemoteUserRepository,

    private val localRoleRepository: RoomRoleDataSource,
    private val remoteRoleRepository: RemoteRoleRepository

) : ViewModel() {

    private val _allMessage = MutableLiveData<Resource<List<MessageResponse>>>()

    private val _allGroup = MutableLiveData<Resource<List<Group>>>()

    private val _allUser = MutableLiveData<Resource<List<UserRequest>>>()

    private val _allRole = MutableLiveData<Resource<List<Role>>>()

    private val _lastUser = MutableLiveData<Resource<User?>>()

    private val _lastGroup = MutableLiveData<Resource<Group?>>()

    private val _lastMessage = MutableLiveData<Resource<Message?>>()

    private val _finish = MutableLiveData<Resource<Boolean>>()
    val finish : LiveData<Resource<Boolean>> get() = _finish

    private val usersGroups = mutableListOf<Pair<Int, Int>>()

    //////////////////////////////////////////////////////////////////////
    // FUNCION DE INICIO
    fun toInit() {

        viewModelScope.launch {
            getAllLastData()
            if (_lastUser.value?.status == Resource.Status.SUCCESS
                && _lastGroup.value?.status == Resource.Status.SUCCESS
                && _lastMessage.value?.status == Resource.Status.SUCCESS) {
                Log.d("p1", "GetAllLastData")
                getAllData()
                if (_allMessage.value?.status == Resource.Status.SUCCESS
                    && _allUser.value?.status == Resource.Status.SUCCESS
                    && _allGroup.value?.status == Resource.Status.SUCCESS) {
                    Log.d("p1", "getAllData")
                    setAllData()
                }
                _finish.value = Resource.success(true)
            }

        }

    }

    ///////////////////////////////////////////////////////////////////////
    // RECOGIDA DE LOS ULTIMOS DATOS DE ROOM
    private suspend fun getAllLastData() {
        _lastUser.value = getLastUser()
        _lastGroup.value = getLastGroup()
        _lastMessage.value = getLastMessage()
    }

    private suspend fun getLastMessage(): Resource<Message?> {
        return withContext(Dispatchers.IO) {
            messageLocalRepository.getLastMessage()
        }
    }

    private suspend fun getLastGroup(): Resource<Group?> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.getLastGroup()
        }
    }

    private suspend fun getLastUser(): Resource<User?> {
        return withContext(Dispatchers.IO) {
            userLocalRepository.getLastUser()
        }
    }

    private suspend fun setAllRoles() {
        return withContext(Dispatchers.IO) {
//            val allRole = _allRole.value?.data
//            if (allRole != null) {
            val roles = listOf(
                Role(2, "PROFESOR"),
                Role(3, "ALUMNO")
            )
            for (role in roles) {
                localRoleRepository.createRole(role)
            }
//            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // LLAMADAS A BBDD REMOTA PARA POBLAR ROOM
    private suspend fun getAllData() {
//        _allRole.value = getAllRoles()
        _allUser.value = getAllUsers(_lastUser.value?.data)
        _allGroup.value = getAllGroups(_lastGroup.value?.data)
        _allMessage.value = getAllMessages(_lastMessage.value?.data)
    }

    private suspend fun getAllMessages(message: Message?): Resource<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            if (message != null) {
                remoteMessageRepository.getMessages(message.id)
            } else {
                remoteMessageRepository.getMessages(null)
            }
        }
    }

    private suspend fun getAllUsers(user: User?): Resource<List<UserRequest>> {
        return withContext(Dispatchers.IO) {
            if (user != null) {
                remoteUserRepository.findUsers(user.id)
            } else {
                remoteUserRepository.findUsers(null)
            }
        }
    }

    private suspend fun getAllGroups(group: Group?): Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            if (group != null) {
                remoteGroupRepository.getGroups(group.id)
            } else {
                remoteGroupRepository.getGroups(null)
            }

        }
    }

    private suspend fun getAllRoles(): Resource<List<Role>> {
        return withContext(Dispatchers.IO) {
            remoteRoleRepository.getRoles()
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // INSERTS EN ROOM DE LA INFORMACION RECOGIDA EN REMOTO
    private suspend fun setAllData() {
        setAllRoles()
        setAllUsers()
        setAllGroups()
        setAllUsersToGroups()
        setAllMessages()
    }

    private suspend fun setAllUsers() {
        return withContext(Dispatchers.IO) {
            val allUser = _allUser.value?.data
            if (allUser != null) {
                for (userRequest in allUser) {
                    val user = User(userRequest.id, userRequest.name, userRequest.surname, userRequest.email, userRequest.phoneNumber, userRequest.roleId)
                    userLocalRepository.createUser(user)
                    for (int in userRequest.chatId) {
                        if (user.id != null) {
                            usersGroups.add(Pair(int, user.id!!))
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
                    groupLocalRepository.createGroup(group)
                }
            }
        }
    }

    private suspend fun setAllUsersToGroups() {
        return withContext(Dispatchers.IO) {
            for (pares in usersGroups) {
                groupLocalRepository.addUserToGroup(pares.first, pares.second)
            }
        }
    }

    private suspend fun setAllMessages() {
        return withContext(Dispatchers.IO) {
            val allMessage = _allMessage.value?.data
            if (allMessage != null) {
                for (messageResponse in allMessage) {
                    val message = Message(
                        messageResponse.id,
                        messageResponse.text,
                        messageResponse.sent,
                        messageResponse.saved,
                        messageResponse.chatId,
                        messageResponse.userId)
                    messageLocalRepository.createMessage(message)
                }
            }
        }
    }

}

class PopulateLocalDataBaseFactory(
    private val groupLocalRepository: RoomGroupDataSource,
    private val remoteGroupRepository: RemoteGroupRepository,

    private val messageLocalRepository: RoomMessageDataSource,
    private val remoteMessageRepository: RemoteMessageRepository,

    private val userLocalRepository: RoomUserDataSource,
    private val remoteUserRepository: RemoteUserRepository,

    private val localRoleRepository: RoomRoleDataSource,
    private val remoteRoleRepository: RemoteRoleRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return PopulateLocalDataBase(groupLocalRepository, remoteGroupRepository,
            messageLocalRepository, remoteMessageRepository,
            userLocalRepository, remoteUserRepository,
            localRoleRepository, remoteRoleRepository) as T
    }

}