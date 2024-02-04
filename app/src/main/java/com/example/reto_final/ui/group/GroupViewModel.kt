package com.example.reto_final.ui.group

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.model.UserChatInfo
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupRepository
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupViewModel(
    private val localGroupRepository: RoomGroupDataSource,
    private val remoteGroupRepository: RemoteGroupRepository,
    private var context: Context) : ViewModel() {

     private val _group = MutableLiveData<Resource<List<Group>>>()
    val group : LiveData<Resource<List<Group>>> get() = _group

    private val _create = MutableLiveData<Resource<Group>>()
    val create : LiveData<Resource<Group>> get() = _create

    private val _delete = MutableLiveData<Resource<Void>>()
    val delete : LiveData<Resource<Void>> get() = _delete

    private val _groupPermission = MutableLiveData<Resource<Int>>()
    val groupPermission : LiveData<Resource<Int>> get() = _groupPermission

    private val _groupPermissionToDelete = MutableLiveData<Resource<Int>>()
    val groupPermissionToDelete : LiveData<Resource<Int>> get() = _groupPermissionToDelete

    private val _userHasAlreadyInGroup = MutableLiveData<Resource<Int>>()
    val userHasAlreadyInGroup : LiveData<Resource<Int>> get() = _userHasAlreadyInGroup

    private val _joinGroup = MutableLiveData<Resource<UserChatInfo>>()
    val joinGroup : LiveData<Resource<UserChatInfo>> get() = _joinGroup

    private val _addUserToGroup = MutableLiveData<Resource<UserChatInfo>>()
    val addUserToGroup : LiveData<Resource<UserChatInfo>> get() = _addUserToGroup

    private val _leaveGroup = MutableLiveData<Resource<UserChatInfo>>()
    val leaveGroup : LiveData<Resource<UserChatInfo>> get() = _leaveGroup

    private val _throwOutFromChat = MutableLiveData<Resource<UserChatInfo>>()
    val throwOutFromChat : LiveData<Resource<UserChatInfo>> get() = _throwOutFromChat

    fun updateGroupList() {
        viewModelScope.launch {
            _group.value = getGroups()
        }
    }

    private suspend fun getGroups() : Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            localGroupRepository.getGroups()
        }
    }

    // FUNCIONES CREATE GROUP
    fun onCreate(name:String, chatEnumType: String, idAdmin: Int) {
        viewModelScope.launch {
             if (InternetChecker.isNetworkAvailable(context)) {
                 val createdGroup = createRemote(name, chatEnumType, idAdmin)
                 if (createdGroup.status == Resource.Status.SUCCESS) {
                     _create.value = createdGroup.data?.let { createLocal(it) }
                 } else {
                     _create.value = Resource.error("Ha ocurrido un error, el grupo no se ha creado")
                 }
            } else {
                 _create.value = Resource.error("Ha ocurrido un error, comprueba tu conexión a internet")
            }
        }
    }

    private suspend fun createRemote(name:String, chatEnumType:String, idAdmin: Int) : Resource<Group> {
        return withContext(Dispatchers.IO) {
            val group = Group(null, name, chatEnumType, null, null, idAdmin)
            remoteGroupRepository.createGroup(group)
        }
    }

    private suspend fun createLocal(group : Group) : Resource<Group> {
        return withContext(Dispatchers.IO) {
            localGroupRepository.createGroup(group)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    // FUNCIONES DELETE GROUP

    fun onDelete(group: Group) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                val deleted = softDeleteRemote(group)
                if (deleted.status == Resource.Status.SUCCESS) {
                    _delete.value = softDeleteLocal(group)
                } else {
                    _delete.value = Resource.error("Ha ocurrido un error, el grupo no se ha eliminado")
                }
            } else {
                _delete.value = Resource.error("Ha ocurrido un error, comprueba tu conexión a internet")
            }

        }
    }

    private suspend fun softDeleteRemote(group: Group) : Resource<Void> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.softDeleteGroup(group.id!!)
        }
    }

    private suspend fun softDeleteLocal(group: Group) : Resource<Void> {
        return withContext(Dispatchers.IO) {
            localGroupRepository.softDeleteGroup(group)
        }
    }

    //////////////////////////////////////////////////////////////////////////////


    // FUNCIONES PERMISOS ENTRAR AL GRUPO
    fun onUserHasPermission(idGroup: Int, idLoginUser: Int) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                _groupPermission.value = userHasPermissionRemote(idGroup)
            } else {
                _groupPermission.value = userHasPermissionLocal(idGroup, idLoginUser)
            }
        }
    }

    private suspend fun userHasPermissionRemote(idGroup: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.canEnterUserChat(idGroup)
        }
    }

    private suspend fun userHasPermissionLocal(idGroup: Int, idLoginUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            localGroupRepository.userHasPermission(idGroup, idLoginUser)
        }
    }

    /////////////////////////////////////////////////////////////////////////////////

    // FUNCIONES PERMISOS ELIMINAR GRUPO
    fun onUserHasPermissionToDelete(idGroup: Int) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                _groupPermissionToDelete.value = userHasPermissionToDeleteRemote(idGroup)
            } else {
                _groupPermissionToDelete.value = Resource.error("Ha ocurrido un error, comprueba tu conexión a internet")
            }
        }
    }
    private suspend fun userHasPermissionToDeleteRemote(idGroup: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.countByAndAdminId(idGroup)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    // FUNCIONES VALIDACION USUARIO YA ESTA EN EL GRUPO
    fun onUserHasAlreadyInGroup(idGroup: Int, idUser: Int) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                _userHasAlreadyInGroup.value = userHasAlreadyInGroupRemote(idGroup)
            } else{
                _userHasAlreadyInGroup.value = userHasAlreadyInGroup(idGroup, idUser)
            }
        }
    }
    private suspend fun userHasAlreadyInGroupRemote(idGroup: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.existsByIdAndUsersId(idGroup)
        }
    }
    private suspend fun userHasAlreadyInGroup(idGroup: Int, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            localGroupRepository.userHasAlreadyInGroup(idGroup, idUser)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////

    // FUNCIONES AÑADIR USUARIO A UN GRUPO
    fun onAddUserToGroup(idGroup: Int, idUser: Int) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                _addUserToGroup.value = addUserToGroupRemote(idGroup, idUser)
                if (_addUserToGroup.value!!.status == Resource.Status.SUCCESS) {
                    _addUserToGroup.value!!.data?.let { addUserToGroupLocal(it.chatId, it.userId , it.joined, null) }
                } else {
                    _addUserToGroup.value = Resource.error("Ha ocurrido un error, no has unir al usuario al grupo")
                }
            } else {
                _addUserToGroup.value = Resource.error("Ha ocurrido un error, comprueba tu conexión a internet")
            }
        }
    }

    private suspend fun addUserToGroupRemote(idGroup: Int, idUser: Int) : Resource<UserChatInfo> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.addUserToChat(idGroup, idUser)
        }
    }

    private suspend fun addUserToGroupLocal(idGroup: Int, idUser: Int, joined: Long, deleted: Long?) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            val userChatInfo = UserChatInfo(idGroup, idUser, joined, deleted)
            localGroupRepository.addUserToGroup(userChatInfo)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////

    // FUNCIONES UNIRSE A UN GRUPO

    fun onJoinGroup(idGroup: Int) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                _joinGroup.value = joinToChat(idGroup)
                if (_joinGroup.value!!.status == Resource.Status.SUCCESS && MyApp.userPreferences.getUser()?.id != null) {
                    //pasamos el userchatinfo
                    _joinGroup.value!!.data?.let { joinGroupLocal(it) }
                } else {
                    _joinGroup.value = Resource.error("Ha ocurrido un error, no has podido unirte al grupo")
                }
            } else {
                _joinGroup.value = Resource.error("Ha ocurrido un error, comprueba tu conexión a internet")
            }
        }
    }
    private suspend fun joinToChat(idGroup: Int) : Resource<UserChatInfo> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.joinToChat(idGroup)
        }
    }
    private suspend fun joinGroupLocal(userChatInfo: UserChatInfo) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            localGroupRepository.addUserToGroup(userChatInfo)
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////

    // FUNCIONES SALIR DE UN GRUPO

    fun onLeaveGroup(idGroup: Int, idUser: Int) {
        viewModelScope.launch {

            val leaveGroup = leaveGroupLocal(idGroup, idUser)

            if (InternetChecker.isNetworkAvailable(context) && leaveGroup.data == 1) {
                _leaveGroup.value = leaveGroupRemote(idGroup)
                Log.d("LLEGGGGGO", "holahola")
            }
        }
    }
    private suspend fun leaveGroupLocal(idGroup: Int, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            localGroupRepository.leaveGroup(idGroup, idUser)
        }
    }
    private suspend fun leaveGroupRemote(idGroup: Int) : Resource<UserChatInfo> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.leaveChat(idGroup)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////

    // FUNCIONES EXPULSAR USUARIO DE UN GRUPO

    fun onChatThrowOut(idGroup: Int, idUser: Int) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                _throwOutFromChat.value = chatThrowOut(idGroup, idUser)
            } else {
                _joinGroup.value = Resource.error("Ha ocurrido un error, comprueba tu conexión a internet")
            }
        }
    }
    private suspend fun chatThrowOut(idGroup: Int, idUser: Int) : Resource<UserChatInfo> {
        return withContext(Dispatchers.IO) {
            Log.d("LOLO", idGroup.toString() + idUser.toString())
            remoteGroupRepository.chatThrowOut(idGroup, idUser)
        }
    }

}

class RoomGroupViewModelFactory(
    private val roomGroupRepository: RoomGroupDataSource,
    private val remoteGroupRepository: RemoteGroupDataSource,
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return GroupViewModel(roomGroupRepository, remoteGroupRepository, context) as T
    }

}