package com.example.reto_final.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.AuthRequest
import com.example.reto_final.data.User
import com.example.reto_final.data.repository.CommonUserRepository
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userRepository: CommonUserRepository) : ViewModel() {

    private val _user= MutableLiveData<Resource<User>>()
    val user : LiveData<Resource<User>> get() = _user
    private val _update= MutableLiveData<Resource<Void>>()
    val update : LiveData<Resource<Void>> get() = _update

    private suspend fun searchUser(email:String, password:String) : Resource<User> {
        return withContext(Dispatchers.IO) {
            val user = AuthRequest(email, password, "Nombre")
            userRepository.login(user)
        }
    }
    fun onSearchUser(email:String, password:String) {
        viewModelScope.launch {
            _user.value = searchUser(email,password)
        }
    }

    private  suspend fun logOut() : Resource<Void> {
        return withContext(Dispatchers.IO) {
            userRepository.logout()
        }
    }

    fun onLogOut() {
        viewModelScope.launch {
            logOut()
        }
    }

    fun onChangePassword(password: String) {
        viewModelScope.launch {
            _update.value = changePassword(password)
        }
    }

    private suspend fun changePassword(password: String) : Resource<Void> {
        return withContext(Dispatchers.IO) {
            userRepository.changePassword(password)
        }
    }

}
class UserViewModelFactory(
    private val userRepository: CommonUserRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return UserViewModel(userRepository) as T
    }

}