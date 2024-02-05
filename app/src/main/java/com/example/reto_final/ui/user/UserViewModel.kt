package com.example.reto_final.ui.user

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.model.user.User
import com.example.reto_final.data.repository.local.CommonUserRepository
import com.example.reto_final.data.repository.remote.RemoteUserRepository
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userRepository: CommonUserRepository,
                    private val remoteUserRepository: RemoteUserRepository,
                    private val context:Context) : ViewModel() {

    private val _users = MutableLiveData<Resource<List<User>>>()
    val users : LiveData<Resource<List<User>>> get() = _users

    private val _usersGroup = MutableLiveData<Resource<List<User>>>()
    val usersGroup : LiveData<Resource<List<User>>> get() = _usersGroup

    private val _delete = MutableLiveData<Resource<Void>>()
    val delete :LiveData<Resource<Void>> get() = _delete

    private suspend fun users() : Resource<List<User>> {
        return withContext(Dispatchers.IO) {
            userRepository.getUsers()
        }
    }
    fun onUsers() {
        viewModelScope.launch {
            val response = users()
            _users.value = response
        }
    }
    private suspend fun usersGroupRemote(idGroup: Int) : Resource<List<User>> {
        return withContext(Dispatchers.IO) {
            remoteUserRepository.getUserByChatId(idGroup)
        }
    }
    private suspend fun usersGroup(idGroup: Int) : Resource<List<User>> {
        return withContext(Dispatchers.IO) {
            userRepository.getUsersFromGroup(idGroup)
        }
    }
    fun onUsersGroup(idGroup: Int) {
        viewModelScope.launch {
            val p1 = usersGroup(idGroup)
            Log.d("p1", "${p1.data}")
            _usersGroup.value = p1
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

}
class RoomUserViewModelFactory(
    private val userRepository: CommonUserRepository,
    private val remoteUserRepository:RemoteUserRepository,
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return UserViewModel(userRepository, remoteUserRepository, context) as T
    }

}