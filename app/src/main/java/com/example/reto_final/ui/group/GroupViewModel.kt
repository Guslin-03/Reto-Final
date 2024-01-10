package com.example.reto_final.ui.group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.Group
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

    init {
        viewModelScope.launch {
            getGRoups()
        }


    }
    private suspend fun create(name:String, groupType: GroupType) : Resource<Group> {
        return withContext(Dispatchers.IO) {
            val group = Group(null, name, groupType)
            Log.d("pr1", "0")
            groupLocalRepository.createGroup(group)
        }
    }
    private suspend fun getGRoups() : Resource<List<Group>> {
        return withContext(Dispatchers.IO) {
            groupLocalRepository.getGroups()
        }
    }
    fun onCreate(name:String, groupType: GroupType) {
        viewModelScope.launch {
            val prueba = create(name, groupType)
            Log.d("pr1", "5")
            Log.d("pr1", prueba.toString())
            _create.value = Resource.success(true)
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