package com.example.reto_final.ui.group

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupRepository
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupViewModel(
    private val groupLocalRepository: RoomGroupDataSource,
    private val remoteGroupRepository: RemoteGroupRepository,
    private var context: Context) : ViewModel() {

     private val _group = MutableLiveData<Resource<List<Group>>>()
    val group : LiveData<Resource<List<Group>>> get() = _group

    private val _create = MutableLiveData<Resource<Void>>()
    val create : LiveData<Resource<Void>> get() = _create

    private val _delete = MutableLiveData<Resource<Boolean>>()
    val delete : LiveData<Resource<Boolean>> get() = _delete

    private val _groupPermission = MutableLiveData<Resource<Boolean>>()
    val groupPermission : LiveData<Resource<Boolean>> get() = _groupPermission

    private val _groupPermissionToDelete = MutableLiveData<Resource<Boolean>>()
    val groupPermissionToDelete : LiveData<Resource<Boolean>> get() = _groupPermissionToDelete

    private val _userHasAlreadyInGroup = MutableLiveData<Resource<Boolean>>()
    val userHasAlreadyInGroup : LiveData<Resource<Boolean>> get() = _userHasAlreadyInGroup

    private val _addUserToGroup = MutableLiveData<Resource<Boolean>>()
    val addUserToGroup : LiveData<Resource<Boolean>> get() = _addUserToGroup

    private val _leaveGroup = MutableLiveData<Resource<Int>>()
    val leaveGroup : LiveData<Resource<Int>> get() = _leaveGroup

    fun updateGroupList() {
        viewModelScope.launch {
            _group.value = getGroups()
        }
    }

    private suspend fun getGroups() : Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.getGroups()
        }
    }
    private suspend fun getGroupsRemote() : Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.getGroups()
        }
    }

    private suspend fun createRemote(name:String, chatEnumType:String, idAdmin: Int) : Resource<Void> {
        return withContext(Dispatchers.IO) {
            val group = Group(null, name, chatEnumType, idAdmin)
            remoteGroupRepository.createGroup(group)
        }
    }


    fun onCreate(name:String, chatEnumType: String, idAdmin: Int) {
        viewModelScope.launch {
             if (InternetChecker.isNetworkAvailable(context)) {
                 _create.value = createRemote(name, chatEnumType, idAdmin)
            }
        }
    }

    private suspend fun deleteRemote(group: Group) : Resource<Void> {
        return withContext(Dispatchers.IO) {
                remoteGroupRepository.deleteGroup(group.id!!)
        }
    }
    fun onDelete(group: Group) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                deleteRemote(group)
                _delete.value = Resource.success(true)
            }
        }
    }

    private suspend fun userHasPermission(idGroup: Int, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.userHasPermission(idGroup, idUser)
        }
    }
    private suspend fun userHasPermissionRemote(idGroup: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.canEnterUserChat(idGroup)
        }
    }
    fun onUserHasPermission(idGroup: Int, idUser: Int) {
        viewModelScope.launch {
            val result = if (InternetChecker.isNetworkAvailable(context)) {
                userHasPermissionRemote(idGroup)
            }else{
                userHasPermission(idGroup, idUser)
            }
            if (result.data == 1) {
                _groupPermission.value = Resource.success(true)
            }else {
                _groupPermission.value = Resource.error("No permission")
            }
        }
    }
    private suspend fun userHasPermissionToDelete(idGroup: Int, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.userHasPermissionToDelete(idGroup, idUser)
        }
    }
    private suspend fun userHasPermissionToDeleteRemote(idGroup: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.countByAndAdminId(idGroup)
        }
    }
    fun onUserHasPermissionToDelete(idGroup: Int, idUser: Int) {
        viewModelScope.launch {
            val result = userHasPermissionToDeleteRemote(idGroup)

            if (result.data == 1) {
                _groupPermissionToDelete.value = Resource.success(true)
            }else {
                _groupPermissionToDelete.value = Resource.error("No permission")
            }
        }
    }
    private suspend fun userHasAlreadyInGroupRemote(idGroup: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.existsByIdAndUsers_Id(idGroup)
        }
    }
    private suspend fun userHasAlreadyInGroup(idGroup: Int, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.userHasAlreadyInGroup(idGroup, idUser)
        }
    }
    fun onUserHasAlreadyInGroup(idGroup: Int, idUser: Int) {
        viewModelScope.launch {
            val result = if (InternetChecker.isNetworkAvailable(context)) {
                userHasAlreadyInGroupRemote(idGroup)
            }else{
                userHasAlreadyInGroup(idGroup, idUser)
            }
            if (result.data == 1) {
                _userHasAlreadyInGroup.value = Resource.success(true)
            } else {
                _userHasAlreadyInGroup.value = Resource.error("Is not on Group")
            }
        }
    }

    private suspend fun addUserToGroupRemote(idGroup: Int, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.addUserToChat(idGroup, idUser)
        }
    }

    fun onAddUserToGroup(idGroup: Int, idUser: Int) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                val result = addUserToGroupRemote(idGroup, idUser)
                if (result.data != 0) {
                    _addUserToGroup.value = Resource.success(true)
                }else {
                    _addUserToGroup.value = Resource.error("Ha ocurrido un error, no has podido unirte al grupo")
                }
            }
        }
    }

    fun onJoinGroup(idGroup: Int) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                val result = joinGroup(idGroup)
                if (result.data != 0) {
                    _addUserToGroup.value = Resource.success(true)
                }else {
                    _addUserToGroup.value = Resource.error("Ha ocurrido un error, no has podido unirte al grupo")
                }
            }
        }
    }

    private suspend fun joinGroup(idGroup: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.joinGroup(idGroup)
        }
    }

    private suspend fun leaveGroup(idGroup: Int, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.leaveGroup(idGroup, idUser)
        }
    }
    private suspend fun leaveGroupRemote(idGroup: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            remoteGroupRepository.leaveChat(idGroup)
        }
    }
    fun onLeaveGroup(idGroup: Int, idUser: Int) {
        viewModelScope.launch {
            _leaveGroup.value = if (InternetChecker.isNetworkAvailable(context)) {
                leaveGroupRemote(idGroup)
            } else {
                leaveGroup(idGroup, idUser)
            }
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