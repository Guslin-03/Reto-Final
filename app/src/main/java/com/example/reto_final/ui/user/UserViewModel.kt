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

    private suspend fun usersGroup(idGroup: Int?) : Resource<List<User>> {
        return withContext(Dispatchers.IO) {
            userRepository.getUsersFromGroup(idGroup)
        }
    }
    fun onUsersGroup(idGroup: Int?) {
        viewModelScope.launch {
            val response = usersGroup(idGroup)
            _usersGroup.value = usersGroup(idGroup)
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