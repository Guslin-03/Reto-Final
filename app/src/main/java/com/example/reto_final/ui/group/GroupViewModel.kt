package com.example.reto_final.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.local.group.GroupType
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupViewModel(private val groupLocalRepository: RoomGroupDataSource) : ViewModel() {

    private val _group = MutableLiveData<Resource<List<Group>>>()
    val group : LiveData<Resource<List<Group>>> get() = _group

    private val _create = MutableLiveData<Resource<Boolean>>()
    val create : LiveData<Resource<Boolean>> get() = _create

    private val _delete = MutableLiveData<Resource<Boolean>>()
    val delete : LiveData<Resource<Boolean>> get() = _delete

    private val _groupPermission = MutableLiveData<Resource<Boolean>>()
    val groupPermission : LiveData<Resource<Boolean>> get() = _groupPermission

    init { updateGroupList() }
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
    private suspend fun create(name:String, groupType: GroupType, idAdmin: Int) : Resource<Group> {
        return withContext(Dispatchers.IO) {
            val group = Group(null, name, groupType, idAdmin)
            groupLocalRepository.createGroup(group)
        }
    }
    fun onCreate(name:String, groupType: GroupType, idAdmin: Int) {
        viewModelScope.launch {
            create(name, groupType, idAdmin)
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

}

class RoomGroupViewModelFactory(
    private val roomGroupRepository: RoomGroupDataSource
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return GroupViewModel(roomGroupRepository) as T
    }

}