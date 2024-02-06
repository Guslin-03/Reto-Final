package com.example.reto_final.data.repository.local

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.Role
import com.example.reto_final.data.model.group.GroupResponse
import com.example.reto_final.data.model.group.PendingGroupRequest
import com.example.reto_final.data.model.user.User
import com.example.reto_final.data.model.userGroup.UserChatInfo
import com.example.reto_final.data.model.user.UserRequest
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.message.MessageResponse
import com.example.reto_final.data.model.message.PendingMessageRequest
import com.example.reto_final.data.repository.local.group.DbGroup
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.message.MessageEnumClass
import com.example.reto_final.data.repository.local.group.toGroup
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.data.repository.local.role.RoomRoleDataSource
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
import com.example.reto_final.data.repository.local.user.UserRoleType
import com.example.reto_final.data.repository.remote.RemoteGroupRepository
import com.example.reto_final.data.repository.remote.RemoteMessageRepository
import com.example.reto_final.data.repository.remote.RemoteRoleRepository
import com.example.reto_final.data.repository.remote.RemoteUserRepository
import com.example.reto_final.data.socket.SocketMessageReq
import com.example.reto_final.ui.message.FileManager
import com.example.reto_final.utils.MyApp
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

    private val _allPendingMessages = MutableLiveData<Resource<List<MessageResponse>>>()

    private val _pendingMessage = MutableLiveData<Resource<List<Message>>>()

    private val _allPendingGroups = MutableLiveData<Resource<List<GroupResponse>>>()

    private val _pendingGroup = MutableLiveData<Resource<List<Group>>>()

    private val _finish = MutableLiveData<Resource<Boolean>>()
    val finish : LiveData<Resource<Boolean>> get() = _finish

    private val userChatInfo = mutableListOf<UserChatInfo>()
    private val fileManager: FileManager = FileManager(MyApp.context)

    //////////////////////////////////////////////////////////////////////
    // FUNCION DE INICIO
    fun toInit() {

        viewModelScope.launch {
            getAllLastData()
//            Log.d("p1", "${_lastGroup.value?.status}")
//            Log.d("p1", "${_lastMessage.value?.data}")
//            Log.d("p1", "${_lastUser.value?.status}")
//            Log.d("p1", "${_pendingMessage.value?.status}")
            if (_lastUser.value?.status == Resource.Status.SUCCESS
                && _lastGroup.value?.status == Resource.Status.SUCCESS
                && _lastMessage.value?.status == Resource.Status.SUCCESS
//                && _pendingMessage.value?.status == Resource.Status.SUCCESS
                ) {
//                Log.d("p1", "GetAllLastData")
                getAllData()
////                Log.d("p1", "${_allMessage.value?.data}")
//                Log.d("p1", "${_allUser.value?.data}")
//                Log.d("p1", "${_allGroup.value?.data}")
//                Log.d("p1", "${_allPendingMessages.value?.status}")
                if (_allMessage.value?.status == Resource.Status.SUCCESS
                    && _allUser.value?.status == Resource.Status.SUCCESS
                    && _allGroup.value?.status == Resource.Status.SUCCESS
//                    && _allPendingMessages.value?.status == Resource.Status.SUCCESS
                    ) {
//                    Log.d("p1", "getAllData")
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
//        _pendingMessage.value = getPendingMessages()
//        _pendingGroup.value = getPendingGroups()
    }

    private suspend fun getLastMessage(): Resource<Message?> {
        return withContext(Dispatchers.IO) {
            messageLocalRepository.getLastMessage()
        }
    }

    private suspend fun getPendingGroups(): Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.getPendingGroups()
        }
    }

    private suspend fun getPendingMessages(): Resource<List<Message>> {
        return withContext(Dispatchers.IO) {
            messageLocalRepository.getPendingMessages()
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
                Role(2, UserRoleType.Profesor.toString()),
                Role(3, UserRoleType.Alumno.toString())
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
//        val pendingMessage = _pendingMessage.value?.data
//        val pendingMessageRequest = pendingMessage?.map { it.toPendingMessageRequest()}
//        _allPendingMessages.value = setPendingMessages(pendingMessageRequest)
//        val pendingGroup = _pendingGroup.value?.data
//        val pendingGroupRequest = pendingGroup?.map { it.toPendingGroupRequest()}
//        _allPendingGroups.value = setPendingGroups(pendingGroupRequest)
    }

    private suspend fun getAllMessages(message: Message?): Resource<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            Log.d("HOLA", "ENTRA EN GET ALL")
            if (message != null) {
                Log.d("HOLA", "ENTRA IF")
                remoteMessageRepository.getMessages(message.id)
            } else {
                Log.d("HOLA", "ENTRA ELSE")
                remoteMessageRepository.getMessages(0)
            }
        }
    }

    private suspend fun setPendingMessages(listPendingMessages: List<PendingMessageRequest?>?) : Resource<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            if (listPendingMessages != null) {
                remoteMessageRepository.setPendingMessages(listPendingMessages)
            } else {
                Resource.success()
            }
        }
    }

    private suspend fun setPendingGroups(listPendingGroups: List<PendingGroupRequest?>?) : Resource<List<GroupResponse>> {
        return withContext(Dispatchers.IO) {
            if (listPendingGroups != null) {
                remoteGroupRepository.setPendingGroups(listPendingGroups)
            } else {
                Resource.success()
            }
        }
    }

    private suspend fun getAllUsers(user: User?): Resource<List<UserRequest>> {
        return withContext(Dispatchers.IO) {
            if (user != null) {
                remoteUserRepository.findUsers(user.id)
            } else {
                remoteUserRepository.findUsers(0)
            }
        }
    }

    private suspend fun getAllGroups(group: Group?): Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            if (group != null) {
                remoteGroupRepository.getGroups(group.id)
            } else {
                remoteGroupRepository.getGroups(0)
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
//        updateAllPendingMessages()
        updateAllPendingGroups()
    }

    private suspend fun setAllUsers() {
        return withContext(Dispatchers.IO) {
            val allUser = _allUser.value?.data
            if (allUser != null) {
                for (userRequest in allUser) {
                    Log.d("VENGA", ""+userRequest.phoneNumber1)
                    val user = User(userRequest.id, userRequest.name, userRequest.surname, userRequest.email, userRequest.phoneNumber1, userRequest.roleId)
                    userLocalRepository.createUser(user)
                    userChatInfo.addAll(userRequest.userChatInfo)
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
            for (userChatInfo in userChatInfo) {
                groupLocalRepository.addUserToGroup(userChatInfo)
            }
        }
    }

    private suspend fun setAllMessages() {
        Log.d("HOLA", "LLEGA AL SET")
        return withContext(Dispatchers.IO) {
            val allMessage = _allMessage.value?.data
            if (allMessage != null) {
                Log.d("HOLA", "LLEGA AL IF")
                for (messageResponse in allMessage) {
                    if (messageResponse.type == MessageEnumClass.FILE.toString()) {
                        val filePath = fileManager.saveBase64ToFile(messageResponse.text) // Use the instance

                        val message = Message(
                            messageResponse.id,
                            filePath,
                            messageResponse.sent,
                            messageResponse.saved,
                            messageResponse.type,
                            messageResponse.chatId,
                            messageResponse.userId)
                        messageLocalRepository.createMessage(message)
                    }else{
                        val message = Message(
                            messageResponse.id,
                            messageResponse.text,
                            messageResponse.sent,
                            messageResponse.saved,
                            messageResponse.type,
                            messageResponse.chatId,
                            messageResponse.userId)
                        messageLocalRepository.createMessage(message)
                    }
                }
            }
        }
    }

    private suspend fun updateAllPendingMessages() {
        return withContext(Dispatchers.IO) {
            val allPendingMessagesResponse = _allPendingMessages.value?.data
            if (allPendingMessagesResponse != null) {
                for (pendingMessageResponse in allPendingMessagesResponse) {
                    val pendingMessage = Message(
                        pendingMessageResponse.id,
                        pendingMessageResponse.text,
                        pendingMessageResponse.sent,
                        pendingMessageResponse.saved,
                        pendingMessageResponse.type,
                        pendingMessageResponse.chatId,
                        pendingMessageResponse.userId)
                    messageLocalRepository.updateMessage(pendingMessage)
                }
            }

        }
    }

    private suspend fun updateAllPendingGroups() {
        return withContext(Dispatchers.IO) {
            val allPendingGroupsResponse = _allPendingGroups.value?.data
            if (allPendingGroupsResponse != null) {
                for (pendingGroupResponse in allPendingGroupsResponse) {
                    val pendingGroup = Group(
                        pendingGroupResponse.id,
                        pendingGroupResponse.name,
                        pendingGroupResponse.type,
                        pendingGroupResponse.created,
                        pendingGroupResponse.deleted,
                        pendingGroupResponse.localDeleted,
                        pendingGroupResponse.adminId)
                    groupLocalRepository.updateGroup(pendingGroup)
                }
            }

        }
    }

    private fun Group.toPendingGroupRequest() =
        PendingGroupRequest(
            id,
            name,
            type,
            created,
            deleted,
            adminId)
    private fun Message.toPendingMessageRequest() =
        id?.let { PendingMessageRequest(
            chatId,
            userId,
            it,
            text,
            sent,
            type) }

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