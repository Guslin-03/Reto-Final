package com.example.reto_final.ui.group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupRepository
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupViewModel(private val groupLocalRepository: RoomGroupDataSource, private val remoteGroupRepository: RemoteGroupRepository) : ViewModel() {

    private val _group = MutableLiveData<Resource<List<Group>>>()
    val group : LiveData<Resource<List<Group>>> get() = _group

    private val _create = MutableLiveData<Resource<Boolean>>()
    val create : LiveData<Resource<Boolean>> get() = _create

    private val _delete = MutableLiveData<Resource<Boolean>>()
    val delete : LiveData<Resource<Boolean>> get() = _delete

    private val _groupPermission = MutableLiveData<Resource<Boolean>>()
    val groupPermission : LiveData<Resource<Boolean>> get() = _groupPermission

    private val _groupPermissionToDelete = MutableLiveData<Resource<Boolean>>()
    val groupPermissionToDelete : LiveData<Resource<Boolean>> get() = _groupPermissionToDelete

    private val _userHasAlreadyInGroup = MutableLiveData<Resource<Boolean>>()
    val userHasAlreadyInGroup : LiveData<Resource<Boolean>> get() = _userHasAlreadyInGroup

    private val _AddUserToGroup = MutableLiveData<Resource<Boolean>>()
    val addUserToGroup : LiveData<Resource<Boolean>> get() = _AddUserToGroup

    init { updateGroupList() }
    fun updateGroupList() {
        viewModelScope.launch {
            _group.value = getGroups()
        }
    }
    private suspend fun getGroups() : Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.getGroups()
//            remoteGroupRepository.getGroups()
        }
    }
    private suspend fun create(name:String, chatEnumType:String, idAdmin: Int) : Resource<Group> {
        return withContext(Dispatchers.IO) {
            val group = Group(0,name, chatEnumType, idAdmin)
            //groupLocalRepository.createGroup(group)
            Log.d("prueba",""+group)
            remoteGroupRepository.createGroup(group)
        }
    }
    fun onCreate(name:String, chatEnumType: String, idAdmin: Int) {
        viewModelScope.launch {
            create(name, chatEnumType, idAdmin)
            _create.value = Resource.success(true)
        }
    }
    private suspend fun delete(group: Group) : Resource<Void> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.deleteGroup(group)
        }
    }
    fun onDelete(group: Group) {
        viewModelScope.launch {
            delete(group)
            _delete.value = Resource.success(true)
        }
    }

    private suspend fun userHasPermission(idGroup: Int?, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.userHasPermission(idGroup, idUser)
        }
    }
    fun onUserHasPermission(idGroup: Int?, idUser: Int) {
        viewModelScope.launch {
            val result = userHasPermission(idGroup, idUser)
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
    fun onUserHasPermissionToDelete(idGroup: Int, idUser: Int) {
        viewModelScope.launch {
            val result = userHasPermissionToDelete(idGroup, idUser)
            if (result.data == 1) {
                _groupPermissionToDelete.value = Resource.success(true)
            }else {
                _groupPermissionToDelete.value = Resource.error("No permission")
            }
        }
    }

    private suspend fun userHasAlreadyInGroup(idGroup: Int?, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.userHasAlreadyInGroup(idGroup, idUser)
        }
    }
    fun onUserHasAlreadyInGroup(idGroup: Int?, idUser: Int) {
        viewModelScope.launch {
            val result = userHasAlreadyInGroup(idGroup, idUser)
            if (result.data == 1) {
                _userHasAlreadyInGroup.value = Resource.success(true)
            }else {
                _userHasAlreadyInGroup.value = Resource.error("Is not on Group")
            }
        }
    }

    private suspend fun addUserToGroup(idGroup: Int?, idUser: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.userHasAlreadyInGroup(idGroup, idUser)
        }
    }
    fun onAddUserToGroup(idGroup: Int?, idUser: Int) {
        viewModelScope.launch {
            val result = userHasAlreadyInGroup(idGroup, idUser)
            if (result.data == 1) {
                _userHasAlreadyInGroup.value = Resource.success(true)
            }else {
                _userHasAlreadyInGroup.value = Resource.error("Is not on Group")
            }
        }
    }

}

class RoomGroupViewModelFactory(
    private val roomGroupRepository: RoomGroupDataSource,
    private val remoteGroupRepository: RemoteGroupDataSource
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return GroupViewModel(roomGroupRepository, remoteGroupRepository) as T
    }

}