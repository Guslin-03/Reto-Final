package com.example.reto_final.data.repository.local

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

    private val _allMessage = MutableLiveData<Resource<List<Message>>>()

    private val _allGroup = MutableLiveData<Resource<List<Group>>>()

    private val _allUser = MutableLiveData<Resource<List<UserRequest>>>()

    private val _allRole = MutableLiveData<Resource<List<Role>>>()

    init {
        getAllData()
        if (_allMessage.value?.status == Resource.Status.SUCCESS
            && _allUser.value?.status == Resource.Status.SUCCESS
            && _allGroup.value?.status == Resource.Status.SUCCESS
            && _allRole.value?.status == Resource.Status.SUCCESS) {
            setAllData()
        }
    }

    private fun getAllData() {
        viewModelScope.launch {
            _allMessage.value = getAllMessages()
            _allUser.value = getAllUsers()
            _allGroup.value = getAllGroups()
            _allRole.value = getAllRoles()
        }
    }

    private fun setAllData() {
        viewModelScope.launch{
            setAllMessages()
            setAllGroups()
            setAllUsers()
            setAllRoles()
        }
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
                            groupLocalRepository.addUserToGroup(user.id!!, int)
                        }
                    }
                }
            }
        }
    }

    private suspend fun setAllGroups() {
        return withContext(Dispatchers.IO) {
            val allGroup = _allGroup.value?.data
            if (allGroup != null) {
                for (group in allGroup) {
                    groupLocalRepository.createGroup(group)
                }
            }
        }
    }

    private suspend fun setAllMessages() {
        return withContext(Dispatchers.IO) {
            val allMessage = _allMessage.value?.data
            if (allMessage != null) {
                for (message in allMessage) {
                    messageLocalRepository.createMessage(message)
                }
            }
        }
    }

    private suspend fun setAllRoles() {
        return withContext(Dispatchers.IO) {
            val allRole = _allRole.value?.data
            if (allRole != null) {
                for (role in allRole) {
                    localRoleRepository.createRole(role)
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