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

class UserViewModel(
    private val userRepository: CommonUserRepository
) : ViewModel() {

//    private val _created = MutableLiveData<Resource<Integer>>()
//    val created : LiveData<Resource<Integer>> get() = _created
//    private val _found= MutableLiveData<Resource<User>>()
//    val found : LiveData<Resource<User>> get() = _found
//    private val _update= MutableLiveData<Resource<Void>>()
//    val update : LiveData<Resource<Void>> get() = _update
    private val _user= MutableLiveData<Resource<User>>()
    val user : LiveData<Resource<User>> get() = _user

//    private suspend fun createUser(user : User) : Resource<Integer> {
//        return withContext(Dispatchers.IO) {
//            userRepository.signIn(user)
//        }
//    }
//    fun onCreateUser(user: User) {
//        viewModelScope.launch {
//            _created.value = createUser(user)
//        }
//    }
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

//    fun onUpdateUser(email: String, oldPassword: String, password: String) {
//        viewModelScope.launch {
//            _update.value = updateUser(email, oldPassword, password)
//        }
//    }
//
//    private suspend fun updateUser(email: String, oldPassword: String, password: String) : Resource<Void> {
//        return withContext(Dispatchers.IO) {
//            val changePasswordRequest = ChangePasswordRequest(email,oldPassword,password)
//            userRepository.updateUser(changePasswordRequest)
//        }
//    }
    fun getUserInfo(){
        viewModelScope.launch {
            _user.value = getInfo()
        }
    }
    private suspend fun getInfo() : Resource<User> {
        return withContext(Dispatchers.IO) {
            userRepository.getUserInfo()
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