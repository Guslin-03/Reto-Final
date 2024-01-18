package com.example.reto_final.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.User
import com.example.reto_final.data.repository.local.CommonUserRepository
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userRepository: CommonUserRepository) : ViewModel() {

    private val _usersGroup = MutableLiveData<Resource<List<User>>>()
    val usersGroup : LiveData<Resource<List<User>>> get() = _usersGroup

    private val _delete = MutableLiveData<Resource<Void>>()
    val delete :LiveData<Resource<Void>> get() = _delete

    private val _isAdmin = MutableLiveData<Resource<Void>>()
    val isAdmin : LiveData<Resource<Void>> get() = _isAdmin

    private suspend fun usersGroup(idGroup: Int?) : Resource<List<User>> {
        return withContext(Dispatchers.IO) {
            userRepository.getUsersFromGroup(idGroup)
        }
    }
    fun onUsersGroup(idGroup: Int?) {
        viewModelScope.launch {
            val response = usersGroup(idGroup)
            _usersGroup.value = response
        }
    }

    private suspend fun delete(userId: Int, groupId: Int) : Resource<Void> {
        return withContext(Dispatchers.IO) {
            userRepository.deleteUserFromGroup(userId, groupId)
        }
    }
    fun onDelete(userId: Int, groupId: Int) {
        viewModelScope.launch {
            val response = delete(userId, groupId)
            _delete.value = response
        }
    }

    private suspend fun userIsAdmin(idUser: Int, idGroup: Int) : Resource<Int> {
        return withContext(Dispatchers.IO) {
            userRepository.userIsAdmin(idUser, idGroup)
        }
    }
    fun onUserIsAdmin(idUser: Int, idGroup: Int) {
        viewModelScope.launch {
            val response = userIsAdmin(idUser, idGroup)
            if (response.data == 1) {
                _isAdmin.value = Resource.success()
            }
        }
    }

}
class RoomUserViewModelFactory(
    private val userRepository: CommonUserRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return UserViewModel(userRepository) as T
    }

}